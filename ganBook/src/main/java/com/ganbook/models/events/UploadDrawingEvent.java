package com.ganbook.models.events;

import com.ganbook.models.DrawingAnswer;

public class UploadDrawingEvent {

    public DrawingAnswer drawingAnswer;


    public UploadDrawingEvent(DrawingAnswer drawingAnswer) {
        this.drawingAnswer = drawingAnswer;
    }

    public DrawingAnswer getDrawingAnswer() {
        return drawingAnswer;
    }

    public void setDrawingAnswer(DrawingAnswer drawingAnswer) {
        this.drawingAnswer = drawingAnswer;
    }

    @Override
    public String toString() {
        return "UploadDrawingEvent{" +
                "drawingAnswer=" + drawingAnswer +
                '}';
    }
}
