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
package org.apache.click.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * The <code>LineStyleProvider</code> implementation for the Velocity template.
 * <p>
 * TODO multi-line comment (#* ... *#)
 *
 * @author Naoki Takezoe
 */
public class LineStyleProviderForVelocity extends LineStyleProviderForHTML {

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection styleRanges) {
		List<Object> results = new ArrayList<Object>();
		boolean result = super.prepareRegions(currentRegion, start, length, results);

        // TODO Is it possible to update colors when preferences changed...?
    	Color colorVariable  = ClickPlugin.getDefault().getColorManager().get(ClickPlugin.PREF_COLOR_VAR);
    	Color colorDirective = ClickPlugin.getDefault().getColorManager().get(ClickPlugin.PREF_COLOR_DIR);
    	Color colorComment   = ClickPlugin.getDefault().getColorManager().get(ClickPlugin.PREF_COLOR_CMT);

        IStructuredDocument document = getDocument();

        for(Iterator ite = results.iterator(); ite.hasNext();){
            StyleRange styleRange = (StyleRange)ite.next();

            IStructuredDocumentRegion region = document.getRegionAtCharacterOffset(styleRange.start);
            String text = region.getText();
            int mStart = styleRange.start - region.getStartOffset();
            int mEnd = mStart + styleRange.length;
            for(IStructuredDocumentRegion chkRegion = region.getPrevious(); chkRegion != null; chkRegion = chkRegion.getPrevious()){
                String type = chkRegion.getType();
                if(!type.equals(DOMRegionContext.XML_CONTENT) && !type.equals(DOMRegionContext.UNDEFINED)){
                    break;
                }
                text = chkRegion.getText() + text;
                mStart += chkRegion.getLength();
                mEnd   += chkRegion.getLength();
            }

            for(IStructuredDocumentRegion chkRegion = region.getNext(); chkRegion != null; chkRegion = chkRegion.getNext()){
                String type = chkRegion.getType();
                if(!type.equals(DOMRegionContext.XML_CONTENT) && !type.equals(DOMRegionContext.UNDEFINED)){
                    break;
                }
                text = (new StringBuilder(String.valueOf(text))).append(chkRegion.getText()).toString();
            }

            Pattern p = Pattern.compile("##.*|#[a-z]+|\\$((\\{.*?\\})|([a-zA-Z0-9\\-_]*))");
            Matcher m = p.matcher(text);
            int pos = 0;
            while(m.find()) {
            	Color color = m.group().startsWith("##") ? colorComment :
            		          m.group().startsWith("#") ? colorDirective : colorVariable;

                if(m.start() < mStart){
                    if(m.end() < mStart){
                        continue;
                    }
                    StyleRange curr;
                    if(m.end() < mEnd){
                        curr = (StyleRange)styleRange.clone();
                        curr.start = styleRange.start;
                        curr.length = m.end() - mStart;
                        curr.foreground = color;
                        styleRanges.add(curr);
                        pos = m.end() - mStart;
                        continue;
                    }
                    curr = (StyleRange)styleRange.clone();
                    curr.foreground = color;
                    styleRanges.add(curr);
                    pos = m.end();
                    break;
                }
                if(m.start() >= mEnd){
                    break;
                }
                if(m.start() > mStart && pos == 0){
                    StyleRange prev = (StyleRange)styleRange.clone();
                    prev.start = styleRange.start;
                    prev.length = m.start() - mStart;
                    styleRanges.add(prev);
                    pos = m.end() - mStart;
                }
                if(m.end() < mEnd){
                    StyleRange curr = (StyleRange)styleRange.clone();
                    curr.start = (styleRange.start + m.start()) - mStart;
                    curr.length = m.end() - m.start();
                    curr.foreground = color;
                    styleRanges.add(curr);
                    pos = m.end() - mStart;
                } else {
                    StyleRange curr = (StyleRange)styleRange.clone();
                    curr.start = (styleRange.start + m.start()) - mStart;
                    curr.length = (styleRange.length - m.start()) + mStart;
                    curr.foreground = color;
                    styleRanges.add(curr);
                    pos = m.end() - mStart;
                }
            }
            if(pos < styleRange.length){
                StyleRange post = (StyleRange)styleRange.clone();
                post.start = styleRange.start + pos;
                post.length = styleRange.length - pos;
                styleRanges.add(post);
            }
            //styleRanges.remove(styleRange);
        }

        return result;
	}

}
