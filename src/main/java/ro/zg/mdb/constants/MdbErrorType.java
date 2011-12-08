/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.constants;

public enum MdbErrorType {
    REQUIRED, GET_FIELD_ERROR, SET_FIELD_ERROR, UNKNOWN_FIELD, OBJECT_MATERIALIZATION_ERROR, VALIDATION_ERROR, UNKNOWN_SEQUENCE, UNIQUENESS_VIOLATED, PERSISTENCE_ERROR, UPDATE_ERROR, DUPLICATE_UNIQUE_VALUE, NO_PK_DEFINED, UNKNOWN_OBJECT_TYPE, ONE_TO_ONE_VIOLATED, MULTIPLE_OBEJCT_ID_FIELDS, WRONG_FIELD_TYPE, INVALID_CONSTRAINT, DIRECT_REFERENCE_VIOLATED;
}
