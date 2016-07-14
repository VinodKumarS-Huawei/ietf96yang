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

package org.onosproject.yang.gen.v1.test.test.rev20151216;

import java.util.List;
import org.onosproject.yang.gen.v1.test.test.rev20151216.test.Cont1;
import org.onosproject.yang.gen.v1.test.test.rev20151216.test.List1;

/**
 * Abstraction of an entity which represents the functionality of test.
 */
public interface Test {

    /**
     * Returns the attribute list1.
     *
     * @return list of list1
     */
    List<List1> getList1();

    /**
     * Returns the attribute cont1.
     *
     * @return value of cont1
     */
    Cont1 getCont1();

    /**
     * Builder for test.
     */
    interface TestBuilder {

        /**
         * Returns the attribute list1.
         *
         * @return list of list1
         */
        List<List1> getList1();

        /**
         * Returns the attribute cont1.
         *
         * @return value of cont1
         */
        Cont1 getCont1();

        /**
         * Sets the value to attribute list1.
         *
         * @param list1 list of list1
         */
        void setList1(List<List1> list1);

        /**
         * Sets the value to attribute cont1.
         *
         * @param cont1 value of cont1
         */
        void setCont1(Cont1 cont1);

        /**
         * Builds object of test.
         *
         * @return object of test.
         */
        Test build();
    }
}