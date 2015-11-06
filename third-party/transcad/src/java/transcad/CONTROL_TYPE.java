/*
 * Copyright  2007 PB 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package transcad;


class CONTROL_TYPE
{

    private CONTROL_TYPE()
    {
        System.out.println("Error: You are calling the TransCAD matrix stub code.");
        System.out.println("Set your classpath to c:\\program files\\TransCAD\\GISDK\\Matrices");
        throw new RuntimeException();
   }

    public static byte CONTROL_FALSE;
    public static byte CONTROL_TRUE;
    public static byte CONTROL_NEVER;
    public static byte CONTROL_ALWAYS;
    public static byte CONTROL_AUTOMATIC;

}
