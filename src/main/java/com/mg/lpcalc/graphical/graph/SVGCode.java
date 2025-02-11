package com.mg.lpcalc.graphical.graph;

public class SVGCode {

    public static final String SVG_HEADER = """
            <style>
                .axis {
                    font-size: 6px;
                    paint-order: stroke fill;
                    transform: scale(1, -1);
                    stroke: white;
                    stroke-width: 2;
                }
            </style>
            <svg xmlns='http://www.w3.org/2000/svg' transform='scale(1, -1)' viewBox='%.2f %.2f %.2f %.2f' width='%d' height='%d'>
            """;

    public static final String LINE = "<line x1='%.2f' y1='%.2f' x2='%.2f' y2='%.2f' stroke='%s' stroke-width='%.2f'/>\n";

    public static final String GROUP = "<g transform='translate(%.2f, %.2f)'>\n";

    public static final String POLYGON = "<polygon fill='%s' opacity='%.2f' points='%s'/>\n";

    public static final String CIRCLE = "<circle cx='%.2f' cy='%.2f' r='%.2f' fill='%s'/>\n";

    public static final String TEXT = "<text x='%.2f' y='%.2f' class='%s'>%s</text>\n";

}
