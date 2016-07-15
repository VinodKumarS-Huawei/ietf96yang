/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.yangutils.parser.impl.listeners;

import org.onosproject.yangutils.datamodel.YangAppDataStructure;
import org.onosproject.yangutils.datamodel.YangCompilerAnnotation;
import org.onosproject.yangutils.datamodel.YangDataStructure;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.TreeWalkListener;

import static org.onosproject.yangutils.datamodel.YangDataStructure.getType;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.APP_DATA_STRUCTURE;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *  container-stmt      = container-keyword sep identifier-arg-str optsep
 *                        (";" /
 *                         "{" stmtsep
 *                             ;; these stmts can appear in any order
 *                             [when-stmt stmtsep]
 *                             *(if-feature-stmt stmtsep)
 *                             *(must-stmt stmtsep)
 *                             [presence-stmt stmtsep]
 *                             [config-stmt stmtsep]
 *                             [status-stmt stmtsep]
 *                             [description-stmt stmtsep]
 *                             [reference-stmt stmtsep]
 *                             *((typedef-stmt /
 *                                grouping-stmt) stmtsep)
 *                             *(data-def-stmt stmtsep)
 *                         "}")
 *
 * ANTLR grammar rule
 *  containerStatement : CONTAINER_KEYWORD identifier
 *                   (STMTEND | LEFT_CURLY_BRACE (whenStatement | ifFeatureStatement | mustStatement |
 *                   presenceStatement | configStatement | statusStatement | descriptionStatement |
 *                   referenceStatement | typedefStatement | groupingStatement
 *                    | dataDefStatement)* RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "container"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class AppDataStructureListener {

    /**
     * Creates a new container listener.
     */
    private AppDataStructureListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar rule
     * (container), performs validation and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processAppDataStructureEntry(TreeWalkListener listener,
                                                    GeneratedYangParser.AppDataStructureStatementContext ctx) {

        checkStackIsNotEmpty(listener, MISSING_HOLDER, APP_DATA_STRUCTURE, "", ENTRY);
        YangAppDataStructure appDataStructure = new YangAppDataStructure();
        YangDataStructure dataStructure = getType(ctx.appDataStructure().getText());
        appDataStructure.setDataStructure(dataStructure);
        // TODO : set prefix

        Parsable curData = listener.getParsedDataStack().peek();
        if (curData instanceof YangCompilerAnnotation) {
            YangCompilerAnnotation compilerAnnotation = ((YangCompilerAnnotation) curData);
            compilerAnnotation.setYangAppDataStructure(appDataStructure);
            listener.getParsedDataStack().push(appDataStructure);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, APP_DATA_STRUCTURE,
                    "", ENTRY));
        }
    }

    /**
     * It is called when parser exits from grammar rule (container), it perform
     * validations and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processAppDataStructureExit(TreeWalkListener listener,
                                                   GeneratedYangParser.AppDataStructureStatementContext ctx) {

        checkStackIsNotEmpty(listener, MISSING_HOLDER, APP_DATA_STRUCTURE, "", EXIT);
        if (!(listener.getParsedDataStack().peek() instanceof YangAppDataStructure)) {
            throw new ParserException(constructListenerErrorMessage(MISSING_CURRENT_HOLDER, APP_DATA_STRUCTURE,
                    "", EXIT));
        }
        listener.getParsedDataStack().pop();
    }
}
