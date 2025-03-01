package com.mg.lpcalc.graphical.graph;

public class SVGCode {

    public static final String SVG_METADATA = """
            <style>
                text {
                  font-size: 6px;
                  paint-order: stroke fill;
                  transform: scale(1, -1);
                  stroke: white;
                  stroke-width: 2;
                }
                .no-stroke {
                  stroke: none;
                  stroke-width: 2;
                }
                .line-label {
                  font-size: 5px;
                }
            </style>
            <svg xmlns='http://www.w3.org/2000/svg' transform='scale(1, -1)' viewBox='%.2f %.2f %.2f %.2f' width='%d' height='%d'>
              <defs>
                <marker id="arrowhead" markerWidth="5" markerHeight="7"\s
                refX="0" refY="1.75" orient="auto">
                  <polygon points="0 0, 5 1.75, 0 3.5" />
                </marker>
                <marker id="arrowhead-red" markerWidth="5" markerHeight="7"\s
                refX="0" refY="1.75" orient="auto">
                  <polygon points="0 0, 5 1.75, 0 3.5" fill="#c20000"/>
                </marker>
              </defs>
            """;

    public static final String LINE = "<line x1='%.2f' y1='%.2f' x2='%.2f' y2='%.2f' stroke='%s' stroke-width='%.2f'/>\n";

    public static final String AXIS = "<line x1='%.2f' y1='%.2f' x2='%.2f' y2='%.2f' stroke='black' marker-end='url(#arrowhead)'/>\n";

    public static final String OBJECTIVE = "<line x1='0' y1='0' x2='%.2f' y2='%.2f' stroke='#c20000' stroke-width='0.7' marker-end='url(#arrowhead-red)'/>\n";

    public static final String GROUP = "<g transform='translate(%.2f, %.2f)'>\n";

    public static final String POLYGON = "<polygon fill='%s' opacity='%.2f' points='%s'/>\n";

    public static final String CIRCLE = "<circle cx='%.2f' cy='%.2f' r='%.2f' fill='%s'/>\n";

    public static final String TEXT = "<text x='%.2f' y='%.2f' class='%s'>%s</text>\n";

}
