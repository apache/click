package net.sf.clickide.ui.editor;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * The <code>LineStyleProvider</code> implementation for the Velocity template.
 * 
 * @author Naoki Takezoe
 */
public class LineStyleProviderForVelocity extends LineStyleProviderForHTML {

	private IStructuredDocument document;
	
	public void init(IStructuredDocument document, Highlighter highlighter) {
		super.init(document, highlighter);
		this.document = document;
	}
	
	public boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection styleRanges) {
        boolean result = super.prepareRegions(currentRegion, start, length, styleRanges);
        
		Color var = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
		Color dir = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
		Color comment = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
        Object sr[] = styleRanges.toArray();
		
        for(int i = 0; i < sr.length; i++)
        {
            StyleRange styleRange = (StyleRange)sr[i];
            IStructuredDocumentRegion region = document.getRegionAtCharacterOffset(styleRange.start);
            String text = region.getText();
            int mStart = styleRange.start - region.getStartOffset();
            int mEnd = mStart + styleRange.length;
            for(IStructuredDocumentRegion chkRegion = region.getPrevious(); chkRegion != null; chkRegion = chkRegion.getPrevious()){
                String type = chkRegion.getType();
                if(!type.equals(DOMRegionContext.XML_CONTENT) && !type.equals(DOMRegionContext.UNDEFINED))
                    break;
                text = chkRegion.getText() + text;
                mStart += chkRegion.getLength();
                mEnd += chkRegion.getLength();
            }

            for(IStructuredDocumentRegion chkRegion = region.getNext(); chkRegion != null; chkRegion = chkRegion.getNext()){
                String type = chkRegion.getType();
                if(!type.equals(DOMRegionContext.XML_CONTENT) && !type.equals(DOMRegionContext.UNDEFINED))
                    break;
                text = (new StringBuilder(String.valueOf(text))).append(chkRegion.getText()).toString();
            }

            Pattern p = Pattern.compile("#.*|\\$((\\{.*?\\})|([a-zA-Z0-9\\-_]*))");
            Matcher m = p.matcher(text);
            int pos = 0;
            while(m.find()) {
            	Color color = m.group().startsWith("##") ? comment : 
            		          m.group().startsWith("#") ? dir : var;
            	
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
            styleRanges.remove(sr[i]);
        }

        return result;
	}
	
	public void release() {
		this.document = null;
		super.release();
	}
	
}
