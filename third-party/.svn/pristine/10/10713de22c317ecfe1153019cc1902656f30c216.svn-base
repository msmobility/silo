/*
 * Copyright 1999,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.xmlrpc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Wraps a <code>DateFormat</code> instance to provide thread safety.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 */
public class DateTool
{
    protected static final String FORMAT = "yyyyMMdd'T'HH:mm:ss";

    private DateFormat df;

    /**
     * Uses the <code>DateFormat</code> string
     * <code>yyyyMMdd'T'HH:mm:ss</code>.
     *
     * @see #FORMAT
     */
    public DateTool()
    {
        df = new SimpleDateFormat(FORMAT);
    }

    /**
     * @param d The date to format.
     * @return The formatted date.
     */
    public synchronized String format(Date d)
    {
        return df.format(d);
    }

    /**
     * @param s The text to parse a date from.
     * @return The parsed date.
     * @exception ParseException If the date could not be parsed.
     */
    public synchronized Date parse(String s)
        throws ParseException
    {
        return df.parse(s);
    }
}
