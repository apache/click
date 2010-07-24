/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.page.general;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.apache.click.Context;
import org.apache.click.ActionResult;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.ClickUtils;
import org.apache.commons.io.IOUtils;

/**
 * This demo shows to invoke a PAGE_ACTION using an HTML <img> tag. The pageAction
 * will render the image data to the browser.
 */
public class PageActionImage extends BorderPage {

    private static final long serialVersionUID = 1L;

    /**
     * This page method is invoked from the <img> element and returns an ActionResult
     * instance containing the static image data.
     */
    public ActionResult getStaticImageData() {
        // Load the static image 'click-icon-blue-32.png'
        byte[] imageData = loadImageData("click-icon-blue-32.png");

        // Lookup the contentType for a PNG image
        String contentType = ClickUtils.getMimeType("png");

        // Return an ActionResult containing the image data
        return new ActionResult(imageData, contentType);
    }

    /**
     * This page method is invoked from the <img> element and returns an ActionResult
     * instance containing the image data specified by the imageName parameter.
     */
    public ActionResult getDynamicImageData() {
        Context context = getContext();

        // Retrieve the image name parameter from the request
        String imageName = context.getRequestParameter("imageName");

        // Load the static image 'click-icon-blue-32.png'
        byte[] imageData = loadImageData(imageName);

        // Lookup the contentType for a PNG image
        String contentType = ClickUtils.getMimeType("png");

        // Return an ActionResult containing the image data
        return new ActionResult(imageData, contentType);
    }

    private byte[] loadImageData(String imageName) {
        try {
            ServletContext servletContext = getContext().getServletContext();
            InputStream is = servletContext.getResourceAsStream("/assets/images/" + imageName);
            byte[] imageData = IOUtils.toByteArray(is);
            return imageData;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
