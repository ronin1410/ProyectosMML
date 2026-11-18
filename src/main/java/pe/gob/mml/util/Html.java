package pe.gob.mml.util;

/** Utilitario para escapar texto antes de mostrarlo en el JSP (evita XSS). */
public class Html {

    public static String esc(Object valor) {
        if (valor == null) return "";
        String s = valor.toString();
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
