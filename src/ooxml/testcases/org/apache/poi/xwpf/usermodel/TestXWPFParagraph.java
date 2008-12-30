/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.xwpf.usermodel;

import java.io.File;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.openxmlformats.schemas.officeDocument.x2006.math.STSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextAlignment;

/**
 * Tests for XWPF Paragraphs
 */
public class TestXWPFParagraph extends TestCase {
    /**
     * A simple file
     */
    private XWPFDocument xml;
    private File file;
/*
    protected void setUp() throws Exception {
        super.setUp();

        file = new File(
                System.getProperty("HWPF.testdata.path") +
                File.separator + "ThreeColHead.docx"
        );
        assertTrue(file.exists());
        xml = new XWPFDocument(POIXMLDocument.openPackage(file.toString()));
    }
*/
    /**
     * Check that we get the right paragraph from the header
     */
    /*
    public void testHeaderParagraph() throws Exception {
        XWPFHeader hdr = xml.getHeaderFooterPolicy().getDefaultHeader();
        assertNotNull(hdr);

        XWPFParagraph[] ps = hdr.getParagraphs();
        assertEquals(1, ps.length);
        XWPFParagraph p = ps[0];

        assertEquals(5, p.getCTP().getRArray().length);
        assertEquals("First header column!\tMid header\tRight header!", p
                .getText());
    }
*/
    /**
     * Check that we get the right paragraphs from the document
     */
    /*
    public void testDocumentParagraph() throws Exception {
        XWPFParagraph[] ps = xml.getParagraphs();
        assertEquals(10, ps.length);

        assertFalse(ps[0].isEmpty());
        assertEquals(
                "This is a sample word document. It has two pages. It has a three column heading, but no footer.",
                ps[0].getText());

        assertTrue(ps[1].isEmpty());
        assertEquals("", ps[1].getText());

        assertFalse(ps[2].isEmpty());
        assertEquals("HEADING TEXT", ps[2].getText());

        assertTrue(ps[3].isEmpty());
        assertEquals("", ps[3].getText());

        assertFalse(ps[4].isEmpty());
        assertEquals("More on page one", ps[4].getText());
    }
*/
    public void testSetGetBorderTop() {
        //new clean instance of paragraph
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        assertEquals(STBorder.NONE.intValue(), p.getBorderTop().getValue());
        
        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();
        
        //bordi
        CTPBdr bdr = ppr.addNewPBdr();
        CTBorder borderTop = bdr.addNewTop();
        borderTop.setVal(STBorder.DOUBLE);
        bdr.setTop(borderTop);
        
        assertEquals(Borders.DOUBLE, p.getBorderTop());
        p.setBorderTop(Borders.SINGLE);
        assertEquals(STBorder.SINGLE, borderTop.getVal());
    }

    public void testSetGetAlignment() {
        //new clean instance of paragraph
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        assertEquals(STJc.LEFT.intValue(), p.getAlignment().getValue());
        
        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        CTJc align = ppr.addNewJc();
        align.setVal(STJc.CENTER);
        assertEquals(ParagraphAlignment.CENTER, p.getAlignment());

        p.setAlignment(ParagraphAlignment.BOTH);
        assertEquals(STJc.BOTH, ppr.getJc().getVal());
    }

    
    public void testSetGetSpacing() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        assertEquals(-1, p.getSpacingAfter());
        
        CTSpacing spacing = ppr.addNewSpacing();
        spacing.setAfter(new BigInteger("10"));
        assertEquals(10, p.getSpacingAfter());

        p.setSpacingAfter(100);
        assertEquals(100, spacing.getAfter().intValue());
    }

    public void testSetGetSpacingLineRule() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        assertEquals(STLineSpacingRule.INT_AUTO, p.getSpacingLineRule().getValue());
        
        CTSpacing spacing = ppr.addNewSpacing();
        spacing.setLineRule(STLineSpacingRule.AT_LEAST);
        assertEquals(LineSpacingRule.AT_LEAST, p.getSpacingLineRule());

        p.setSpacingAfter(100);
        assertEquals(100, spacing.getAfter().intValue());
    }
    
    public void testSetGetIndentation() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        assertEquals(-1, p.getIndentationLeft());
        
        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        assertEquals(-1, p.getIndentationLeft());
        
        CTInd ind = ppr.addNewInd();
        ind.setLeft(new BigInteger("10"));
        assertEquals(10, p.getIndentationLeft());

        p.setIndentationLeft(100);
        assertEquals(100, ind.getLeft().intValue());
    }
    
    public void testSetGetVerticalAlignment() {
        //new clean instance of paragraph
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        CTTextAlignment txtAlign = ppr.addNewTextAlignment();
        txtAlign.setVal(STTextAlignment.CENTER);
        assertEquals(TextAlignment.CENTER, p.getVerticalAlignment());

        p.setVerticalAlignment(TextAlignment.BOTTOM);
        assertEquals(STTextAlignment.BOTTOM, ppr.getTextAlignment().getVal());
    }

    public void testSetGetWordWrap() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        CTOnOff wordWrap = ppr.addNewWordWrap();
        wordWrap.setVal(STOnOff.FALSE);
        assertEquals(false, p.isWordWrap());

        p.setWordWrap(true);
        assertEquals(STOnOff.TRUE, ppr.getWordWrap().getVal());
    }


    public void testSetGetPageBreak() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();

        CTP ctp = p.getCTP();
        CTPPr ppr = ctp.getPPr()== null? ctp.addNewPPr() : ctp.getPPr();

        CTOnOff pageBreak = ppr.addNewPageBreakBefore();
        pageBreak.setVal(STOnOff.FALSE);
        assertEquals(false, p.isPageBreak());

        p.setPageBreak(true);
        assertEquals(STOnOff.TRUE, ppr.getPageBreakBefore().getVal());
    }


}