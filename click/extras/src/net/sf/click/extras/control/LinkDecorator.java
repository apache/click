/*
 * Copyright 2006 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import java.util.HashMap;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Decorator;
import net.sf.click.control.PageLink;
import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * Provides a table column AbstractLink Decorator.
 *
 * @author Malcolm Edgar
 */
public class LinkDecorator implements Decorator {

    /** The array of AbstractLinks to render. */
    protected AbstractLink[] linksArray;

    /** The row object identifier property. */
    protected String idProperty;

    /** The OGNL context map. */
    protected Map ognlContext;

    /**
     * Create a new AbstractLink table column Decorator with the given actionLink
     * and row object identifier property name.
     *
     * @param link the AbstractLink to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(AbstractLink link, String idProperty) {
        if (link == null) {
            throw new IllegalArgumentException("Null actionLink parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.linksArray = new AbstractLink[1];
        this.linksArray[0] = link;
        this.idProperty = idProperty;
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * AbstractLinks array and row object identifier property name.
     *
     * @param links the array of AbstractLinks to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(AbstractLink[] links, String idProperty) {
        if (links == null) {
            throw new IllegalArgumentException("Null links parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.linksArray = links;
        this.idProperty = idProperty;
    }

    /**
     * Render the given row object using the links.
     *
     * @see Decorator#render(java.lang.Object, net.sf.click.Context)
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered links for the given row object and request context
     */
    public String render(Object row, Context context) {
        if (ognlContext == null) {
            ognlContext = new HashMap();
        }

        if (linksArray.length == 1) {
            AbstractLink link = linksArray[0];
            link.setContext(context);

            try {
                Object value = Ognl.getValue(idProperty, ognlContext, row);
                if (link instanceof ActionLink) {
                    ((ActionLink) link).setValueObject(value);

                } else if (link instanceof PageLink) {
                    ((PageLink) link).setParameter(idProperty.toString(),
                                                  value.toString());
                }

            } catch (OgnlException ognle) {
                throw new RuntimeException(ognle);
            }

            return link.toString();

        } else {
            HtmlStringBuffer buffer = new HtmlStringBuffer();

            try {
                Object value = Ognl.getValue(idProperty, ognlContext, row);

                for (int i = 0; i < linksArray.length; i++) {
                    AbstractLink link = linksArray[i];
                    link.setContext(context);

                    if (link instanceof ActionLink) {
                        ((ActionLink) link).setValueObject(value);

                    } else if (link instanceof PageLink) {
                        ((PageLink) link).setParameter(idProperty.toString(),
                                                      value.toString());
                    }

                    if (i > 0) {
                        buffer.append(" | ");
                    }

                    buffer.append(link.toString());
                }

            } catch (OgnlException ognle) {
                throw new RuntimeException(ognle);
            }

            return buffer.toString();
        }
    }

}
