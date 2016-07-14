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

package org.onosproject.yangutils.translator.tojava;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.YangTypeHolder;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaType;
import org.onosproject.yangutils.utils.io.impl.YangPluginConfig;

import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.INT64;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.UINT16;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.UINT32;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_FOR_TYPE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FROM_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.OF_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yangutils.translator.tojava.JavaQualifiedTypeInfo.getQualifiedInfoOfFromString;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateTypeDefClassFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateUnionClassFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getOfMethodStringAndJavaDoc;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getTypeConstructorStringAndJavaDoc;
import static org.onosproject.yangutils.translator.tojava.utils.ValidatorTypeForUnionTypes.INT_TYPE_CONFLICT;
import static org.onosproject.yangutils.translator.tojava.utils.ValidatorTypeForUnionTypes.LONG_TYPE_CONFLICT;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCamelCase;

/**
 * Represents implementation of java data type code fragments temporary implementations. Maintains the temp files
 * required specific for user defined data type java snippet generation.
 */
public class TempJavaTypeFragmentFiles
        extends TempJavaFragmentFiles {

    /**
     * File name for of string method.
     */
    private static final String OF_STRING_METHOD_FILE_NAME = "OfString";

    /**
     * File name for construction for special type like union, typedef.
     */
    private static final String CONSTRUCTOR_FOR_TYPE_FILE_NAME = "ConstructorForType";

    /**
     * File name for typedef class file name suffix.
     */
    private static final String TYPEDEF_CLASS_FILE_NAME_SUFFIX = EMPTY_STRING;

    /**
     * File name for generated class file for special type like union, typedef suffix.
     */
    private static final String UNION_TYPE_CLASS_FILE_NAME_SUFFIX = EMPTY_STRING;

    /**
     * Integer index in type list.
     */
    private int intIndex = 0;

    /**
     * UInt index in type list.
     */
    private int uintIndex = 0;

    /**
     * long index in type list.
     */
    private int longIndex = 0;

    /**
     * Ulong index in type list.
     */
    private int uLongIndex = 0;

    /**
     * Temporary file handle for of string method of class.
     */
    private File ofStringImplTempFileHandle;

    /**
     * Temporary file handle for constructor for type class.
     */
    private File constructorForTypeTempFileHandle;

    /**
     * Java file handle for typedef class file.
     */
    private File typedefClassJavaFileHandle;

    /**
     * Java file handle for type class like union, typedef file.
     */
    private File typeClassJavaFileHandle;

    /**
     * Java attribute for int.
     */
    private JavaAttributeInfo intAttribute;

    /**
     * Java attribute for long.
     */
    private JavaAttributeInfo longAttribute;

    /**
     * Java attribute for uInt.
     */
    private JavaAttributeInfo uIntAttribute;

    /**
     * Java attribute for uLong.
     */
    private JavaAttributeInfo uLongAttribute;

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated java file info
     * @throws IOException when fails to create new file handle
     */
    public TempJavaTypeFragmentFiles(JavaFileInfo javaFileInfo)
            throws IOException {

        super(javaFileInfo);

        /*
         * Initialize getterImpl, attributes, hash code, equals and to strings
         * when generation file type matches to typeDef class mask.
         */
        addGeneratedTempFile(OF_STRING_IMPL_MASK);
        addGeneratedTempFile(CONSTRUCTOR_FOR_TYPE_MASK);
        addGeneratedTempFile(FROM_STRING_IMPL_MASK);

        setOfStringImplTempFileHandle(getTemporaryFileHandle(OF_STRING_METHOD_FILE_NAME));
        setConstructorForTypeTempFileHandle(getTemporaryFileHandle(CONSTRUCTOR_FOR_TYPE_FILE_NAME));

    }

    /**
     * Returns type class constructor method's temporary file handle.
     *
     * @return type class constructor method's temporary file handle
     */

    public File getConstructorForTypeTempFileHandle() {
        return constructorForTypeTempFileHandle;
    }

    /**
     * Sets type class constructor method's temporary file handle.
     *
     * @param constructorForTypeTempFileHandle type class constructor method's temporary file handle
     */
    private void setConstructorForTypeTempFileHandle(File constructorForTypeTempFileHandle) {
        this.constructorForTypeTempFileHandle = constructorForTypeTempFileHandle;
    }

    /**
     * Returns java file handle for typedef class file.
     *
     * @return java file handle for typedef class file
     */
    private File getTypedefClassJavaFileHandle() {
        return typedefClassJavaFileHandle;
    }

    /**
     * Sets the java file handle for typedef class file.
     *
     * @param typedefClassJavaFileHandle java file handle
     */
    private void setTypedefClassJavaFileHandle(File typedefClassJavaFileHandle) {
        this.typedefClassJavaFileHandle = typedefClassJavaFileHandle;
    }

    /**
     * Returns java file handle for type class file.
     *
     * @return java file handle for type class file
     */
    private File getTypeClassJavaFileHandle() {
        return typeClassJavaFileHandle;
    }

    /**
     * Sets the java file handle for type class file.
     *
     * @param typeClassJavaFileHandle type file handle
     */
    private void setTypeClassJavaFileHandle(File typeClassJavaFileHandle) {
        this.typeClassJavaFileHandle = typeClassJavaFileHandle;
    }

    /**
     * Returns of string method's temporary file handle.
     *
     * @return of string method's temporary file handle
     */

    public File getOfStringImplTempFileHandle() {
        return ofStringImplTempFileHandle;
    }

    /**
     * Set of string method's temporary file handle.
     *
     * @param ofStringImplTempFileHandle of string method's temporary file handle
     */
    private void setOfStringImplTempFileHandle(File ofStringImplTempFileHandle) {
        this.ofStringImplTempFileHandle = ofStringImplTempFileHandle;
    }

    /**
     * Adds all the type in the current data model node as part of the generated temporary file.
     *
     * @param yangTypeHolder YANG java data model node which has type info, eg union / typedef
     * @param pluginConfig   plugin configurations for naming conventions
     * @throws IOException IO operation fail
     */
    void addTypeInfoToTempFiles(YangTypeHolder yangTypeHolder, YangPluginConfig pluginConfig)
            throws IOException {

        List<YangType<?>> typeList = yangTypeHolder.getTypeList();
        if (typeList != null) {
            for (YangType<?> yangType : typeList) {
                if (!(yangType instanceof YangJavaType)) {
                    throw new TranslatorException("Type does not have Java info");
                }
                JavaAttributeInfo javaAttributeInfo = getAttributeForType(yangType, pluginConfig);
                addJavaSnippetInfoToApplicableTempFiles(javaAttributeInfo,
                        pluginConfig, typeList);
            }
            addTypeConstructor(pluginConfig);
            addMethodsInConflictCase(pluginConfig);
        }
    }

    /**
     * Returns java attribute.
     *
     * @param yangType     YANG type
     * @param pluginConfig plugin configurations
     * @return java attribute
     */
    private JavaAttributeInfo getAttributeForType(YangType yangType, YangPluginConfig pluginConfig) {
        YangJavaType<?> javaType = (YangJavaType<?>) yangType;
        javaType.updateJavaQualifiedInfo(pluginConfig.getConflictResolver());
        String typeName = javaType.getDataTypeName();
        typeName = getCamelCase(typeName, pluginConfig.getConflictResolver());
        return getAttributeInfoForTheData(
                javaType.getJavaQualifiedInfo(),
                typeName, javaType,
                getIsQualifiedAccessOrAddToImportList(javaType.getJavaQualifiedInfo()),
                false);
    }

    /**
     * Adds the new attribute info to the target generated temporary files for union class.
     *
     * @param javaAttributeInfo the attribute info that needs to be added to temporary files
     * @param pluginConfig      plugin configurations
     * @param typeList          type list
     * @throws IOException IO operation fail
     */
    private void addJavaSnippetInfoToApplicableTempFiles(JavaAttributeInfo javaAttributeInfo,
                                                         YangPluginConfig pluginConfig, List<YangType<?>> typeList)
            throws IOException {

        setIntConflict(validateForConflictingIntTypes(typeList));
        setLongConflict(validateForConflictingLongypes(typeList));
        updateAttributeCondition(javaAttributeInfo);
        super.addJavaSnippetInfoToApplicableTempFiles(javaAttributeInfo, pluginConfig);

        if (!isIntConflict() && !isLongConflict()) {
            if ((getGeneratedTempFiles() & OF_STRING_IMPL_MASK) != 0) {
                addOfStringMethod(javaAttributeInfo, pluginConfig);
            }

            if ((getGeneratedTempFiles() & CONSTRUCTOR_FOR_TYPE_MASK) != 0) {
                addTypeConstructor(javaAttributeInfo, pluginConfig);
            }
        }
    }

    /**
     * Adds of, getter and from string method in conflict cases.
     *
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to do IO operations
     */
    private void addMethodsInConflictCase(YangPluginConfig pluginConfig) throws IOException {
        if (isIntConflict()) {
            if (getIntIndex() < getUintIndex()) {
                appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(getIntAttribute(),
                        getGeneratedJavaClassName(), pluginConfig)
                        + NEW_LINE);
                addGetterImpl(getIntAttribute(), pluginConfig);
                addFromStringMethod(getIntAttribute(), pluginConfig);
            } else {
                appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(getuIntAttribute(),
                        getGeneratedJavaClassName(), pluginConfig)
                        + NEW_LINE);
                addGetterImpl(getuIntAttribute(), pluginConfig);
                addFromStringMethod(getuIntAttribute(), pluginConfig);
            }
        }
        if (isLongConflict()) {
            if (getLongIndex() < getuLongIndex()) {
                appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(getLongAttribute(),
                        getGeneratedJavaClassName(), pluginConfig)
                        + NEW_LINE);
                addGetterImpl(getLongAttribute(), pluginConfig);
                addFromStringMethod(getLongAttribute(), pluginConfig);
            } else {
                appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(getuLongAttribute(),
                        getGeneratedJavaClassName(), pluginConfig)
                        + NEW_LINE);
                addGetterImpl(getuLongAttribute(), pluginConfig);
                addFromStringMethod(getuLongAttribute(), pluginConfig);
            }
        }


    }

    /**
     * Adds from string method for conflict case.
     *
     * @param newAttrInfo  new attribute
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to do IO operations
     */
    private void addFromStringMethod(JavaAttributeInfo newAttrInfo, YangPluginConfig pluginConfig) throws IOException {

        JavaQualifiedTypeInfo qualifiedInfoOfFromString = getQualifiedInfoOfFromString(newAttrInfo,
                pluginConfig.getConflictResolver());
            /*
             * Create a new java attribute info with qualified information of
             * wrapper classes.
             */
        JavaAttributeInfo fromStringAttributeInfo = getAttributeInfoForTheData(qualifiedInfoOfFromString,
                newAttrInfo.getAttributeName(),
                newAttrInfo.getAttributeType(),
                getIsQualifiedAccessOrAddToImportList(qualifiedInfoOfFromString), false);

        addFromStringMethod(newAttrInfo, fromStringAttributeInfo);
    }

    /**
     * Adds type constructor.
     *
     * @param attr         attribute info
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to append to temporary file
     */
    private void addTypeConstructor(JavaAttributeInfo attr, YangPluginConfig pluginConfig)
            throws IOException {
        appendToFile(getConstructorForTypeTempFileHandle(), getTypeConstructorStringAndJavaDoc(attr,
                getGeneratedJavaClassName(), pluginConfig) + NEW_LINE);
    }

    /**
     * Adds type constructor.
     *
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to append to temporary file
     */
    private void addTypeConstructor(YangPluginConfig pluginConfig)
            throws IOException {
        if (isIntConflict()) {
            appendToFile(getConstructorForTypeTempFileHandle(), getTypeConstructorStringAndJavaDoc(getIntAttribute(),
                    getuIntAttribute(), getGeneratedJavaClassName(), pluginConfig, INT_TYPE_CONFLICT, getIntIndex()
                            < getUintIndex()) + NEW_LINE);
        }
        if (isLongConflict()) {
            appendToFile(getConstructorForTypeTempFileHandle(), getTypeConstructorStringAndJavaDoc(getLongAttribute(),
                    getuLongAttribute(), getGeneratedJavaClassName(), pluginConfig, LONG_TYPE_CONFLICT, getLongIndex()
                            < getuLongIndex()) + NEW_LINE);
        }
    }

    /**
     * Adds of string for type.
     *
     * @param attr         attribute info
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to append to temporary file
     */
    private void addOfStringMethod(JavaAttributeInfo attr, YangPluginConfig pluginConfig)
            throws IOException {
        appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(attr,
                getGeneratedJavaClassName(), pluginConfig)
                + NEW_LINE);
    }

    /**
     * Removes all temporary file handles.
     *
     * @param isErrorOccurred when translator fails to generate java files we need to close all open file handles
     *                        include temporary files and java files.
     * @throws IOException when failed to delete the temporary files
     */
    @Override
    public void freeTemporaryResources(boolean isErrorOccurred)
            throws IOException {

        if ((getGeneratedJavaFiles() & GENERATE_TYPEDEF_CLASS) != 0) {
            closeFile(getTypedefClassJavaFileHandle(), isErrorOccurred);
        }

        if ((getGeneratedJavaFiles() & GENERATE_UNION_CLASS) != 0) {
            closeFile(getTypeClassJavaFileHandle(), isErrorOccurred);
        }

        if ((getGeneratedTempFiles() & CONSTRUCTOR_FOR_TYPE_MASK) != 0) {
            closeFile(getConstructorForTypeTempFileHandle(), true);
        }
        if ((getGeneratedTempFiles() & OF_STRING_IMPL_MASK) != 0) {
            closeFile(getOfStringImplTempFileHandle(), true);
        }
        if ((getGeneratedTempFiles() & FROM_STRING_IMPL_MASK) != 0) {
            closeFile(getFromStringImplTempFileHandle(), true);
        }

        super.freeTemporaryResources(isErrorOccurred);

    }

    /**
     * Constructs java code exit.
     *
     * @param fileType generated file type
     * @param curNode  current YANG node
     * @throws IOException when fails to generate java files
     */
    @Override
    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {
        List<String> imports = new ArrayList<>();
        if (isAttributePresent()) {
            imports = getJavaImportData().getImports();
        }

        createPackage(curNode);

        /*
         * Creates type def class file.
         */
        if ((fileType & GENERATE_TYPEDEF_CLASS) != 0) {
            addImportsToStringAndHasCodeMethods(imports, true);
            setTypedefClassJavaFileHandle(getJavaFileHandle(getJavaClassName(TYPEDEF_CLASS_FILE_NAME_SUFFIX)));
            generateTypeDefClassFile(getTypedefClassJavaFileHandle(), curNode, imports);
        }
        /*
         * Creates type class file.
         */
        if ((fileType & GENERATE_UNION_CLASS) != 0) {
            addImportsToStringAndHasCodeMethods(imports, true);
            setTypeClassJavaFileHandle(getJavaFileHandle(getJavaClassName(UNION_TYPE_CLASS_FILE_NAME_SUFFIX)));
            generateUnionClassFile(getTypeClassJavaFileHandle(), curNode, imports);
        }

        /*
         * Close all the file handles.
         */
        freeTemporaryResources(false);
    }

    /**
     * Returns int type index from type list.
     *
     * @return int type index from type list
     */
    public int getIntIndex() {
        return intIndex;
    }

    /**
     * Sets int type index from type list.
     *
     * @param intIndex int type index from type list.
     */
    private void setIntIndex(int intIndex) {
        this.intIndex = intIndex;
    }

    /**
     * Returns uint type index from type list.
     *
     * @return uint type index from type list
     */
    public int getUintIndex() {
        return uintIndex;
    }

    /**
     * Sets uint type index from type list.
     *
     * @param uintIndex uint type index from type list.
     */
    private void setUintIndex(int uintIndex) {
        this.uintIndex = uintIndex;
    }

    /**
     * Returns long type index from type list.
     *
     * @return long type index from type list
     */
    public int getLongIndex() {
        return longIndex;
    }

    /**
     * Sets long type index from type list.
     *
     * @param longIndex long type index from type list.
     */
    private void setLongIndex(int longIndex) {
        this.longIndex = longIndex;
    }

    /**
     * Returns ulong type index from type list.
     *
     * @return ulong type index from type list
     */
    public int getuLongIndex() {
        return uLongIndex;
    }

    /**
     * Sets ulong type index from type list.
     *
     * @param uLongIndex ulong type index from type list.
     */
    private void setuLongIndex(int uLongIndex) {
        this.uLongIndex = uLongIndex;
    }

    /**
     * Validates conflict for int and uint.
     *
     * @param typeList type list
     * @return true if conflict is there
     */
    private boolean validateForConflictingIntTypes(List<YangType<?>> typeList) {
        boolean isIntPresent = false;
        boolean isUintPresent = false;
        for (YangType type : typeList) {
            if (type.getDataType().equals(INT32)) {
                setIntIndex(typeList.indexOf(type));
                isIntPresent = true;
            }
            if (type.getDataType().equals(UINT16)) {
                setUintIndex(typeList.indexOf(type));
                isUintPresent = true;
            }
        }

        return isIntPresent && isUintPresent;
    }

    /**
     * Validates conflict for long and ulong.
     *
     * @param typeList type list
     * @return true if conflict is there
     */
    private boolean validateForConflictingLongypes(List<YangType<?>> typeList) {
        boolean islongPresent = false;
        boolean isUlongPresent = false;
        for (YangType type : typeList) {
            if (type.getDataType().equals(INT64)) {
                setLongIndex(typeList.indexOf(type));
                islongPresent = true;
            }
            if (type.getDataType().equals(UINT32)) {
                setuLongIndex(typeList.indexOf(type));
                isUlongPresent = true;
            }
        }

        return islongPresent && isUlongPresent;
    }

    /**
     * Updates attribute info in case of conflicts.
     *
     * @param javaAttributeInfo java attribute info
     */
    private void updateAttributeCondition(JavaAttributeInfo javaAttributeInfo) {

        if (isIntConflict()) {
            if (javaAttributeInfo.getAttributeType().getDataType() == UINT16) {
                setuIntAttribute(javaAttributeInfo);
            } else if (javaAttributeInfo.getAttributeType().getDataType() == INT32) {
                setIntAttribute(javaAttributeInfo);
            }

        }
        if (isLongConflict()) {
            if (javaAttributeInfo.getAttributeType().getDataType() == UINT32) {
                setuLongAttribute(javaAttributeInfo);
            } else if (javaAttributeInfo.getAttributeType().getDataType() == INT64) {
                setLongAttribute(javaAttributeInfo);
            }

        }
    }

    /**
     * Returns attribute for int.
     *
     * @return attribute for int
     */
    private JavaAttributeInfo getIntAttribute() {
        return intAttribute;
    }

    /**
     * Sets attribute for int.
     *
     * @param intAttribute attribute for int
     */
    private void setIntAttribute(JavaAttributeInfo intAttribute) {
        this.intAttribute = intAttribute;
    }

    /**
     * Returns attribute for long.
     *
     * @return attribute for long
     */
    private JavaAttributeInfo getLongAttribute() {
        return longAttribute;
    }

    /**
     * Sets attribute for long.
     *
     * @param longAttribute attribute for long
     */
    private void setLongAttribute(JavaAttributeInfo longAttribute) {
        this.longAttribute = longAttribute;
    }

    /**
     * Returns attribute for uInt.
     *
     * @return attribute for uInt
     */
    private JavaAttributeInfo getuIntAttribute() {
        return uIntAttribute;
    }

    /**
     * Sets attribute for uInt.
     *
     * @param uIntAttribute attribute for uInt
     */
    private void setuIntAttribute(JavaAttributeInfo uIntAttribute) {
        this.uIntAttribute = uIntAttribute;
    }

    /**
     * Returns attribute for uLong.
     *
     * @return attribute for uLong
     */
    private JavaAttributeInfo getuLongAttribute() {
        return uLongAttribute;
    }

    /**
     * Sets attribute for uLong.
     *
     * @param uLongAttribute attribute for uLong
     */
    private void setuLongAttribute(JavaAttributeInfo uLongAttribute) {
        this.uLongAttribute = uLongAttribute;
    }
}
