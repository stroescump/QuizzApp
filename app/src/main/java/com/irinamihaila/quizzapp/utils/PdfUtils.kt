package com.irinamihaila.quizzapp.utils

import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph


object PdfUtils {
    fun Document.createPDF(title: String, body: String) {
        addDataIntoPDF(title, body)
    }

    private fun Document.addDataIntoPDF(title: String, body: String) {
        val paraGraph = Paragraph()
        val headingFont = Font(Font.FontFamily.HELVETICA, 24F, Font.BOLD)
        paraGraph.add(Paragraph(title, headingFont))
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph(body))
        add(paraGraph)
    }

    private fun addEmptyLines(paragraph: Paragraph, lineCount: Int) {
        for (i in 0 until lineCount) {
            paragraph.add(Paragraph(""))
        }
    }
}