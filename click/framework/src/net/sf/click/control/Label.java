/*
 * Copyright 2005 Malcolm A. Edgar
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
package net.sf.click.control;

/**
 * Provides a Label display control. The Label control performs no server side 
 * processing.
 *
 * @author Malcolm Edgar
 */
public class Label extends Field {

    /**
     * Create a Label display control.
     * 
     * @param label the label display value
     */
    public Label(String label) {
        super(label);
    }
    
    /**
     * Returns true.
     * 
     * @see Field#onProcess()
     */
    public boolean onProcess() {
        return true;
    }
    
    /**
     * Returns the label.
     *
     * @see Object#toString()
     */
    public String toString() {
        return getLabel();
    }
}
