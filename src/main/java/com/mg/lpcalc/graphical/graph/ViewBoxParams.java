package com.mg.lpcalc.graphical.graph;

public class ViewBoxParams {
    double minX;
    double minY;
    double size;

    // minX - минимальная точка X на графике
    // minY - минимальная точка Y на графике
    // size - размер графика
    // pxSize - перевод координат в пиксели
    // padding - отступ от краев
    public ViewBoxParams(double minX, double minY, double size, double pxSize, double paddingPercentage) {
        double paddingPx = (size * (paddingPercentage - 1)) / 2; // отступ по краям
        this.minY = size + minY * pxSize - paddingPx;
        this.minX = size + minX * pxSize - paddingPx;

        // Если ось не попадает во ViewBox
        if (this.minX > 240) this.minX = 240;
        if (this.minY > 240) this.minY = 240;

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
