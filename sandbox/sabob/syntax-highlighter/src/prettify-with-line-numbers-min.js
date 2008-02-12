var PR_keywords={};(function(){var L="abstract bool break case catch char class const const_cast continue default delete deprecated dllexport dllimport do double dynamic_cast else enum explicit extern false float for friend goto if inline int long mutable naked namespace new noinline noreturn nothrow novtable operator private property protected public register reinterpret_cast return selectany short signed sizeof static static_cast struct switch template this thread throw true try typedef typeid typename union unsigned using declaration, directive uuid virtual void volatile while typeof";var E="as base by byte checked decimal delegate descending event finally fixed foreach from group implicit in interface internal into is lock null object out override orderby params readonly ref sbyte sealed stackalloc string select uint ulong unchecked unsafe ushort var";var B="package synchronized boolean implements import throws instanceof transient extends final strictfp native super";var D="debugger export function with NaN Infinity";var A="require sub unless until use elsif BEGIN END";var K="and assert def del elif except exec global lambda not or pass print raise yield False True None";var J="then end begin rescue ensure module when undef next redo retry alias defined";var G="done fi";var I=[L,E,B,D,A,K,J,G];for(var C=0;C<I.length;C++){var H=I[C].split(" ");for(var F=0;F<H.length;F++){if(H[F]){PR_keywords[H[F]]=true}}}}).call(this);var PR_STRING="str";var PR_KEYWORD="kwd";var PR_COMMENT="com";var PR_TYPE="typ";var PR_LITERAL="lit";var PR_PUNCTUATION="pun";var PR_PLAIN="pln";var PR_TAG="tag";var PR_DECLARATION="dec";var PR_SOURCE="src";var PR_ATTRIB_NAME="atn";var PR_ATTRIB_VALUE="atv";var PR_TAB_WIDTH=8;var PR_CREATE_LINE_BOX=true;var PR_LINE_STEPPING=1;function PR_isWordChar(A){return(A>="a"&&A<="z")||(A>="A"&&A<="Z")}function PR_spliceArrayInto(D,B,A,C){D.unshift(A,C||0);try{B.splice.apply(B,D)}finally{D.splice(0,2)}}var REGEXP_PRECEDER_PATTERN=(function(){var C=["!","!=","!==","#","%","%=","&","&&","&&=","&=","(","*","*=","+=",",","-=","->","/","/=",":","::",";","<","<<","<<=","<=","=","==","===",">",">=",">>",">>=",">>>",">>>=","?","@","[","^","^=","^^","^^=","{","|","|=","||","||=","~","break","case","continue","delete","do","else","finally","instanceof","return","throw","try","typeof"];var D="(?:(?:(?:^|[^0-9.])\\.{1,3})|(?:(?:^|[^\\+])\\+)|(?:(?:^|[^\\-])-)";for(var A=0;A<C.length;++A){var B=C[A];if(PR_isWordChar(B.charAt(0))){D+="|\\b"+B}else{D+="|"+B.replace(/([^=<>:&])/g,"\\$1")}}D+="|^)\\s*$";return new RegExp(D)})();var pr_amp=/&/g;var pr_lt=/</g;var pr_gt=/>/g;var pr_quot=/\"/g;function PR_attribToHtml(A){return A.replace(pr_amp,"&amp;").replace(pr_lt,"&lt;").replace(pr_gt,"&gt;").replace(pr_quot,"&quot;")}function PR_textToHtml(A){return A.replace(pr_amp,"&amp;").replace(pr_lt,"&lt;").replace(pr_gt,"&gt;")}var pr_ltEnt=/&lt;/g;var pr_gtEnt=/&gt;/g;var pr_aposEnt=/&apos;/g;var pr_quotEnt=/&quot;/g;var pr_ampEnt=/&amp;/g;function PR_htmlToText(D){var F=D.indexOf("&");if(F<0){return D}for(--F;(F=D.indexOf("&#",F+1))>=0;){var A=D.indexOf(";",F);if(A>=0){var C=D.substring(F+3,A);var E=10;if(C&&C.charAt(0)=="x"){C=C.substring(1);E=16}var B=parseInt(C,E);if(!isNaN(B)){D=(D.substring(0,F)+String.fromCharCode(B)+D.substring(A+1))}}}return D.replace(pr_ltEnt,"<").replace(pr_gtEnt,">").replace(pr_aposEnt,"'").replace(pr_quotEnt,"\"").replace(pr_ampEnt,"&")}function PR_isRawContent(A){return"XMP"==A.tagName}var PR_innerHtmlWorks=null;function PR_getInnerHtml(D){if(null===PR_innerHtmlWorks){var B=document.createElement("PRE");B.appendChild(document.createTextNode("<!DOCTYPE foo PUBLIC \"foo bar\">\n<foo />"));PR_innerHtmlWorks=!/</.test(B.innerHTML)}if(PR_innerHtmlWorks){var C=D.innerHTML;if(PR_isRawContent(D)){C=PR_textToHtml(C)}return C}var A=[];for(var E=D.firstChild;E;E=E.nextSibling){PR_normalizedHtml(E,A)}return A.join("")}function PR_normalizedHtml(E,C){switch(E.nodeType){case 1:var B=E.tagName.toLowerCase();C.push("<",B);for(var D=0;D<E.attributes.length;++D){var A=E.attributes[D];if(!A.specified){continue}C.push(" ");PR_normalizedHtml(A,C)}C.push(">");for(var F=E.firstChild;F;F=F.nextSibling){PR_normalizedHtml(F,C)}if(E.firstChild||!/^(?:br|link|img)$/.test(B)){C.push("</",B,">")}break;case 2:C.push(E.name.toLowerCase(),"=\"",PR_attribToHtml(E.value),"\"");break;case 3:case 4:C.push(PR_textToHtml(E.nodeValue));break}}function PR_tabExpander(C){var A="                ";var B=0;return function(G){var E=null;var J=0;for(var F=0,I=G.length;F<I;++F){var H=G.charAt(F);switch(H){case"\t":if(!E){E=[]}E.push(G.substring(J,F));var D=C-(B%C);B+=D;for(;D>=0;D-=A.length){E.push(A.substring(0,D))}J=F+1;break;case"\n":B=0;break;default:++B}}if(!E){return G}E.push(G.substring(J));return E.join("")}}var pr_chunkPattern=/(?:[^<]+|<!--[\s\S]*?-->|<!\[CDATA\[([\s\S]*?)\]\]>|<\/?[a-zA-Z][^>]*>|<)/g;var pr_commentPrefix=/^<!--/;var pr_cdataPrefix=/^<\[CDATA\[/;var pr_brPrefix=/^<br\b/i;function PR_extractTags(I){var F=I.match(pr_chunkPattern);var H=[];var C=0;var A=[];if(F){for(var E=0,B=F.length;E<B;++E){var G=F[E];if(G.length>1&&G.charAt(0)==="<"){if(pr_commentPrefix.test(G)){continue}if(pr_cdataPrefix.test(G)){H.push(G.substring(9,G.length-3));C+=G.length-12}else{if(pr_brPrefix.test(G)){H.push("\n");C+=1}else{A.push(C,G)}}}else{var D=PR_htmlToText(G);H.push(D);C+=D.length}}}return{source:H.join(""),tags:A}}function PR_createSimpleLexer(C,B){var A={};(function(){var E=C.concat(B);for(var F=E.length;--F>=0;){var I=E[F];var G=I[3];if(G){for(var H=G.length;--H>=0;){A[G.charAt(H)]=I}}}})();var D=B.length;return function(G,N){N=N||0;var H=[N,PR_PLAIN];var I="";var P=0;var O=G;while(O.length){var E;var J=null;var F=A[O.charAt(0)];if(F){var M=O.match(F[1]);J=M[0];E=F[0]}else{for(var K=0;K<D;++K){F=B[K];var L=F[2];if(L&&!L.test(I)){continue}var M=O.match(F[1]);if(M){J=M[0];E=F[0];break}}if(!J){E=PR_PLAIN;J=O.substring(0,1)}}H.push(N+P,E);P+=J.length;O=O.substring(J.length);if(E!==PR_COMMENT&&/\S/.test(J)){I=J}}return H}}var PR_C_STYLE_STRING_AND_COMMENT_LEXER=PR_createSimpleLexer([[PR_STRING,/^\'(?:[^\\\']|\\[\s\S])*(?:\'|$)/,null,"'"],[PR_STRING,/^\"(?:[^\\\"]|\\[\s\S])*(?:\"|$)/,null,"\""],[PR_STRING,/^\`(?:[^\\\`]|\\[\s\S])*(?:\`|$)/,null,"`"]],[[PR_PLAIN,/^(?:[^\'\"\`\/\#]+)/,null," \r\n"],[PR_COMMENT,/^#[^\r\n]*/,null,"#"],[PR_COMMENT,/^\/\/[^\r\n]*/,null],[PR_STRING,/^\/(?:[^\\\*\/]|\\[\s\S])+(?:\/|$)/,REGEXP_PRECEDER_PATTERN],[PR_COMMENT,/^\/\*[\s\S]*?(?:\*\/|$)/,null]]);function PR_splitStringAndCommentTokens(A){return PR_C_STYLE_STRING_AND_COMMENT_LEXER(A)}var PR_C_STYLE_LITERAL_IDENTIFIER_PUNC_RECOGNIZER=PR_createSimpleLexer([],[[PR_PLAIN,/^\s+/,null," \r\n"],[PR_PLAIN,/^[a-z_$@][a-z_$@0-9]*/i,null],[PR_LITERAL,/^0x[a-f0-9]+[a-z]/i,null],[PR_LITERAL,/^(?:\d(?:_\d+)*\d*(?:\.\d*)?|\.\d+)(?:e[+-]?\d+)?[a-z]*/i,null,"123456789"],[PR_PUNCTUATION,/^[^\s\w\.$@]+/,null]]);function PR_splitNonStringNonCommentTokens(A,E){for(var I=0;I<E.length;I+=2){var B=E[I+1];if(B===PR_PLAIN){var C=E[I];var G=I+2<E.length?E[I+2]:A.length;var K=A.substring(C,G);var J=PR_C_STYLE_LITERAL_IDENTIFIER_PUNC_RECOGNIZER(K,C);for(var H=0,D=J.length;H<D;H+=2){var L=J[H+1];if(L===PR_PLAIN){var N=J[H];var M=H+2<D?J[H+2]:K.length;var F=A.substring(N,M);if(F=="."){J[H+1]=PR_PUNCTUATION}else{if(F in PR_keywords){J[H+1]=PR_KEYWORD}else{if(/^@?[A-Z][A-Z$]*[a-z][A-Za-z$]*$/.test(F)){J[H+1]=F.charAt(0)=="@"?PR_LITERAL:PR_TYPE}}}}}PR_spliceArrayInto(J,E,I,2);I+=J.length-2}}return E}var PR_MARKUP_LEXER=PR_createSimpleLexer([],[[PR_PLAIN,/^[^<]+/,null],[PR_DECLARATION,/^<!\w[^>]*(?:>|$)/,null],[PR_COMMENT,/^<!--[\s\S]*?(?:-->|$)/,null],[PR_SOURCE,/^<\?[\s\S]*?(?:\?>|$)/,null],[PR_SOURCE,/^<%[\s\S]*?(?:%>|$)/,null],[PR_SOURCE,/^<(script|style|xmp)\b[^>]*>[\s\S]*?<\/\1\b[^>]*>/i,null],[PR_TAG,/^<\/?\w[^<>]*>/,null]]);var PR_SOURCE_CHUNK_PARTS=/^(<[^>]*>)([\s\S]*)(<\/[^>]*>)$/;function PR_tokenizeMarkup(F){var C=PR_MARKUP_LEXER(F);for(var E=0;E<C.length;E+=2){if(C[E+1]===PR_SOURCE){var G=C[E];var B=E+2<C.length?C[E+2]:F.length;var A=F.substring(G,B);var D=(A.match(PR_SOURCE_CHUNK_PARTS));if(D){C.splice(E,2,G,PR_TAG,G+D[1].length,PR_SOURCE,G+D[1].length+(D[2]||"").length,PR_TAG)}}}return C}var PR_TAG_LEXER=PR_createSimpleLexer([[PR_ATTRIB_VALUE,/^\'[^\']*(?:\'|$)/,null,"'"],[PR_ATTRIB_VALUE,/^\"[^\"]*(?:\"|$)/,null,"\""],[PR_PUNCTUATION,/^[<>\/=]+/,null,"<>/="]],[[PR_TAG,/^[\w-]+/,/^</],[PR_ATTRIB_VALUE,/^[\w-]+/,/^=/],[PR_ATTRIB_NAME,/^[\w-]+/,null],[PR_PLAIN,/^\s+/,null," \r\n"]]);function PR_splitTagAttributes(F,B){for(var D=0;D<B.length;D+=2){var E=B[D+1];if(E===PR_TAG){var H=B[D];var A=D+2<B.length?B[D+2]:F.length;var C=F.substring(H,A);var G=PR_TAG_LEXER(C,H);PR_spliceArrayInto(G,B,D,2);D+=G.length-2}}return B}function PR_splitSourceNodes(A,E){for(var H=0;H<E.length;H+=2){var B=E[H+1];if(B==PR_SOURCE){var C=E[H];var F=H+2<E.length?E[H+2]:A.length;var I=PR_decorateSource(A.substring(C,F));for(var G=0,D=I.length;G<D;G+=2){I[G]+=C}PR_spliceArrayInto(I,E,H,2);H+=I.length-2}}return E}function PR_splitSourceAttributes(A,I){var P=false;for(var M=0;M<I.length;M+=2){var C=I[M+1];if(C===PR_ATTRIB_NAME){var E=I[M];var K=M+2<I.length?I[M+2]:A.length;P=/^on|^style$/i.test(A.substring(E,K))}else{if(C==PR_ATTRIB_VALUE){if(P){var E=I[M];var K=M+2<I.length?I[M+2]:A.length;var N=A.substring(E,K);var B=N.length;var H=(B>=2&&/^[\"\']/.test(N)&&N.charAt(0)===N.charAt(B-1));var D;var F;var J;if(H){F=E+1;J=K-1;D=N}else{F=E+1;J=K-1;D=N.substring(1,N.length-1)}var O=PR_decorateSource(D);for(var L=0,G=O.length;L<G;L+=2){O[L]+=F}if(H){O.push(J,PR_ATTRIB_VALUE);PR_spliceArrayInto(O,I,M+2,0)}else{PR_spliceArrayInto(O,I,M,2)}}P=false}}}return I}function PR_decorateSource(B){var A=PR_splitStringAndCommentTokens(B);A=PR_splitNonStringNonCommentTokens(B,A);return A}function PR_decorateMarkup(B){var A=PR_tokenizeMarkup(B);A=PR_splitTagAttributes(B,A);A=PR_splitSourceNodes(B,A);A=PR_splitSourceAttributes(B,A);return A}function PR_recombineTagsAndDecorations(L,B,E){var G=[];var K=0;var D=null;var H=null;var C=0;var J=0;var F=PR_tabExpander(PR_TAB_WIDTH);function I(M){if(M>K){if(D&&D!==H){G.push("</span>");D=null}if(!D&&H){D=H;G.push("<span class=\"",D,"\">")}var N=PR_textToHtml(F(L.substring(K,M))).replace(/(\r\n?|\n| ) /g,"$1&nbsp;").replace(/\r\n?|\n/g,"<br>");G.push(N);K=M}}while(true){var A;if(C<B.length){if(J<E.length){A=B[C]<=E[J]}else{A=true}}else{A=false}if(A){I(B[C]);if(D){G.push("</span>");D=null}G.push(B[C+1]);C+=2}else{if(J<E.length){I(E[J]);H=E[J+1];J+=2}else{break}}}I(L.length);if(D){G.push("</span>")}return G.join("")}function PR_create_line_box(D,B){var A=document.createElement("DIV");A.setAttribute("class","prettycontainer");var F=document.createElement("PRE");F.setAttribute("class","prettylines");var E="";for(var C=1;C<(B+2);C++){if(C%PR_LINE_STEPPING==0){E+=C}E+="\n"}F.innerHTML=E;D.parentNode.replaceChild(A,D);A.appendChild(F);A.appendChild(D)}function prettyPrintOne(E){try{var G=PR_extractTags(E);var C=G.source;var A=G.tags;var F=/^\s*</.test(C)&&/>\s*$/.test(C);var B=F?PR_decorateMarkup(C):PR_decorateSource(C);return PR_recombineTagsAndDecorations(C,A,B)}catch(D){if("console" in window){console.log(D);console.trace()}return E}}var PR_SHOULD_USE_CONTINUATION=true;function prettyPrint(G){var B=[document.getElementsByTagName("pre"),document.getElementsByTagName("code"),document.getElementsByTagName("xmp")];var F=[];for(var D=0;D<B.length;++D){for(var C=0;C<B[D].length;++C){F.push(B[D][C])}}B=null;var A=0;function E(){var L=(PR_SHOULD_USE_CONTINUATION?new Date().getTime()+250:Infinity);for(;A<F.length&&new Date().getTime()<L;A++){var M=F[A];if(M.className&&M.className.indexOf("prettyprint")>=0){var O=false;for(var I=M.parentNode;I!=null;I=I.parentNode){if((I.tagName=="pre"||I.tagName=="code"||I.tagName=="xmp")&&I.className&&I.className.indexOf("prettyprint")>=0){O=true;break}}if(!O){var N=PR_getInnerHtml(M);var H=N.replace(/[^\n]/g,"").length;N=N.replace(/(?:\r\n?|\n)$/,"");var P=prettyPrintOne(N);if(!PR_isRawContent(M)){M.innerHTML=P}else{var J=document.createElement("PRE");for(var K=0;K<M.attributes.length;++K){var Q=M.attributes[K];if(Q.specified){J.setAttribute(Q.name,Q.value)}}J.innerHTML=P;M.parentNode.replaceChild(J,M);M=J}if(PR_CREATE_LINE_BOX&&M.localName.toLowerCase()=="pre"){PR_create_line_box(M,H)}}}}if(A<F.length){setTimeout(E,250)}else{if(G){G()}}}E()}