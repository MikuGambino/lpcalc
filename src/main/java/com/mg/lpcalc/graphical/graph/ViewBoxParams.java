package com.mg.lpcalc.graphical.graph;

import lombok.Data;

@Data
public class ViewBoxParams {
    private double minX;
    private double minY;
    private double size;

    // minX - минимальная точка X на графике
    // minY - минимальная точка Y на графике
    // size - размер графика
    // pxSize - перевод координат в пиксели
    // padding - отступ от краев
    public ViewBoxParams(double minX, double minY, double size, double pxSize, double paddingPx) {
        this.minY = size + minY * pxSize - paddingPx;
        this.minX = size + minX * pxSize - paddingPx;

        // Если ось не попадает во ViewBox
        if (this.minX > size - 10) this.minX = size - 10;
        if (this.minY > size - 10) this.minY = size - 10;

        this.size = size;
    }

    @Override
    public String toString() {
        return "ViewBoxParams{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", size=" + size +
                '}';
    }
}
