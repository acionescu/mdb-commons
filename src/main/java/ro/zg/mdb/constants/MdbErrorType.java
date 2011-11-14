/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.constants;

public enum MdbErrorType {
    REQUIRED, GET_FIELD_ERROR,SET_FIELD_ERROR, UNKNOWN_FIELD, OBJECT_MATERIALIZATION_ERROR, VALIDATION_ERROR, UNKNOWN_SEQUENCE, UNIQUENESS_VIOLATED, PERSISTENCE_ERROR, UPDATE_ERROR, DUPLICATE_UNIQUE_VALUE, NO_PK_DEFINED, UNKNOWN_OBJECT_TYPE, ONE_TO_ONE_VIOLATED;
}
