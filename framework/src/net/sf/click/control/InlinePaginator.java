/*
 * Copyright 2008 Malcolm A. Edgar
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

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an inline style table paging controls Paginator.
 *
 * @author Malcolm Edgar
 */
public class InlinePaginator extends DefaultPaginator {

    private static final long serialVersionUID = 1L;

    /**
     * @see net.sf.click.control.Paginator#render(HtmlStringBuffer)
     *
     * @param buffer the string buffer to render the paginator to
     */
    public void render(HtmlStringBuffer buffer) {
        final Table table = getTable();

        if (table == null) {
            throw new IllegalStateException("No parent table defined");
        }

        final ActionLink controlLink = table.getControlLink();

        if (table.getSortedColumn() != null) {
            controlLink.setParameter(Table.SORT, null);
            controlLink.setParameter(Table.COLUMN, table.getSortedColumn());
            controlLink.setParameter(Table.ASCENDING, String.valueOf(table.isSortedAscending()));
        } else {
            controlLink.setParameter(Table.SORT, null);
            controlLink.setParameter(Table.COLUMN, null);
            controlLink.setParameter(Table.ASCENDING, null);
        }

        String firstLabel = "";
        String previousLabel = "";

        if (table.getPageNumber() > 0) {
            controlLink.setDisabled(false);
            controlLink.setImageSrc(table.getMessage("table-inline-first-image"));
            controlLink.setParameter(Table.PAGE, String.valueOf(0));
            controlLink.setTitle(table.getMessage("table-first-title"));
            controlLink.setId("control-first");
            firstLabel = controlLink.toString();

            controlLink.setImageSrc(table.getMessage("table-inline-previous-image"));
            controlLink.setParameter(Table.PAGE, String.valueOf(table.getPageNumber() - 1));
            controlLink.setTitle(table.getMessage("table-previous-title"));
            controlLink.setId("control-previous");
            previousLabel = controlLink.toString();

        } else {
            controlLink.setDisabled(true);

            controlLink.setImageSrc(table.getMessage("table-inline-first-disabled-image"));
            controlLink.setParameter(Table.PAGE, null);
            controlLink.setTitle(null);
            controlLink.setId("control-first");
            firstLabel = controlLink.toString();

            controlLink.setImageSrc(table.getMessage("table-inline-previous-disabled-image"));
            controlLink.setParameter(Table.PAGE, null);
            controlLink.setTitle(null);
            controlLink.setId("control-previous");
            previousLabel = controlLink.toString();
        }

        HtmlStringBuffer pagesBuffer =
            new HtmlStringBuffer(table.getNumberPages() * 70);

        // Create sliding window of paging links
        int lowerBound = Math.max(0, table.getPageNumber() - 5);
        int upperBound = Math.min(lowerBound + 10, table.getNumberPages());
        if (upperBound - lowerBound < 10) {
            lowerBound = Math.max(upperBound - 10, 0);
        }

        controlLink.setImageSrc(null);
        controlLink.setDisabled(false);
        String gotoTitle = table.getMessage("table-goto-title");

        for (int i = lowerBound; i < upperBound; i++) {
            String pageNumber = String.valueOf(i + 1);
            if (i == table.getPageNumber()) {
                pagesBuffer.append("<strong>" + pageNumber + "</strong>");

            } else {
                controlLink.setLabel(pageNumber);
                controlLink.setParameter(Table.PAGE, String.valueOf(i));
                controlLink.setTitle(gotoTitle + " " + pageNumber);
                controlLink.setId("control-" + pageNumber);
                pagesBuffer.append(controlLink.toString());
            }

            if (i < upperBound - 1) {
                pagesBuffer.append("&nbsp; ");
            }
        }

        String nextLabel = "";
        String lastLabel = "";

        if (table.getPageNumber() < table.getNumberPages() - 1) {
            controlLink.setDisabled(false);
            controlLink.setImageSrc(table.getMessage("table-inline-next-image"));
            controlLink.setParameter(Table.PAGE, String.valueOf(table.getPageNumber() + 1));
            controlLink.setTitle(table.getMessage("table-next-title"));
            controlLink.setId("control-next");
            nextLabel = controlLink.toString();

            controlLink.setImageSrc(table.getMessage("table-inline-last-image"));
            controlLink.setParameter(Table.PAGE, String.valueOf(table.getNumberPages() - 1));
            controlLink.setTitle(table.getMessage("table-last-title"));
            controlLink.setId("control-last");
            lastLabel = controlLink.toString();

        } else {
            controlLink.setDisabled(true);

            controlLink.setImageSrc(table.getMessage("table-inline-next-disabled-image"));
            controlLink.setParameter(Table.PAGE, null);
            controlLink.setTitle(null);
            controlLink.setId("control-next");
            nextLabel = controlLink.toString();

            controlLink.setImageSrc(table.getMessage("table-inline-last-disabled-image"));
            controlLink.setParameter(Table.PAGE, null);
            controlLink.setTitle(null);
            controlLink.setId("control-last");
            lastLabel = controlLink.toString();
        }

        controlLink.setDisabled(false);
        controlLink.setId(null);
        controlLink.setImageSrc(null);
        controlLink.setTitle(null);

        final String pageLinks = pagesBuffer.toString();

        final String[] args =
            { firstLabel, previousLabel, pageLinks, nextLabel, lastLabel };

        buffer.append(table.getMessage("table-inline-page-links", args));
    }

}
