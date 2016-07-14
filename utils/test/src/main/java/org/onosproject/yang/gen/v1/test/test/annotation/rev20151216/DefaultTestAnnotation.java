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

package org.onosproject.yang.gen.v1.test.test.annotation.rev20151216;

/**
 * Represents the implementation of testAnnotation.
 */
public class DefaultTestAnnotation implements TestAnnotation {


    /**
     * Creates an instance of testAnnotationImpl.
     *
     * @param builderObject builder object of testAnnotation
     */
    public DefaultTestAnnotation(TestAnnotationBuilder builderObject) {
    }


    /**
     * Represents the builder implementation of testAnnotation.
     */
    public class TestAnnotationBuilder implements TestAnnotation.TestAnnotationBuilder {

        @Override
        public TestAnnotation build() {
            return new DefaultTestAnnotation(this);
        }

        /**
         * Creates an instance of testAnnotationBuilder.
         */
        public TestAnnotationBuilder() {
        }
    }
}