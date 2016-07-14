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

import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;
import org.onosproject.yang.gen.v1.test.test.rev20151216.test.Cont1;
import org.onosproject.yang.gen.v1.test.test.rev20151216.test.List1;

/**
 * Represents the implementation of test.
 */
public class DefaultTest implements Test {

    private List<List1> list1;
    private Cont1 cont1;

    @Override
    public List<List1> getList1() {
        //TODO: YANG utils generated code
        return null;
    }

    @Override
    public Cont1 getCont1() {
        //TODO: YANG utils generated code
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(list1, cont1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultTest) {
            DefaultTest other = (DefaultTest) obj;
            return
                 Objects.equals(list1, other.list1) &&
                 Objects.equals(cont1, other.cont1);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
            .add("list1", list1)
            .add("cont1", cont1)
            .toString();
    }

    /**
     * Creates an instance of testImpl.
     *
     * @param builderObject builder object of test
     */
    public DefaultTest(TestBuilder builderObject) {
        this.list1 = builderObject.getList1();
        this.cont1 = builderObject.getCont1();
    }


    /**
     * Represents the builder implementation of test.
     */
    public class TestBuilder implements Test.TestBuilder {

        private List<List1> list1;
        private Cont1 cont1;


        @Override
        public List<List1> getList1() {
            //TODO: YANG utils generated code
            return null;
        }

        @Override
        public Cont1 getCont1() {
            //TODO: YANG utils generated code
            return null;
        }

        @Override
        public void setList1(List<List1> list1) {
            //TODO: YANG utils generated code
        }

        @Override
        public void setCont1(Cont1 cont1) {
            //TODO: YANG utils generated code
        }
        @Override
        public Test build() {
            return new DefaultTest(this);
        }

        /**
         * Creates an instance of testBuilder.
         */
        public TestBuilder() {
        }
    }
}