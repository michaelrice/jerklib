/*
 *
 *
 * Copyright (C) 2005-2008 Yves Zoundi
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 */
package jerklib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 */
public class DateUtils {
    public static String getTime(String timestampToParse) {
        try {
            long epoch = Long.parseLong(timestampToParse);
            String date = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss").format(new Date(
                    1000L * epoch));

            return date;
        } catch (Exception e) {
            return "";
        }
    }
}
