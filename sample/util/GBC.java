/*
 * $Id: GBC.java 117846 2011-05-09 21:28:29Z costa $
 */
package util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Classe "wrapper" para grid-bag-constraints. Exemplo de uso:
 * <p>
 * add(new JButton(action), <b>new GBC(0, 0).both().right(4)</b>);
 * <p>
 * O código acima adiciona o botóo com x = 0, y = 0, com fill = BOTH, e com um
 * objeto 'insets' cujo 'right' tem valor = 4.
 * 
 * @see <a href="https://gridbaglady.dev.java.net/">gridbaglady.dev.java.net</a>
 * 
 * @author Evan Summers (versóo original)
 */
public class GBC extends GridBagConstraints {

  /**
   * Construtor.
   * 
   * @param gridx
   * @param gridy
   * @param anchor
   * @param fill
   * @param insets
   */
  public GBC(int gridx, int gridy, int anchor, int fill, Insets insets) {
    super(gridx, gridy, 1, 1, 0.0, 0.0, anchor, fill, insets, 0, 0);
    fill(fill);
    insets(insets);
  }

  /**
   * Construtor. Recebe as coordenadas (x, y) e os insets; os demais valores
   * permanecem com os defaults:
   * 
   * <pre>
   * anchor = CENTER
   * fill = NONE
   * </pre>
   * 
   * @param gridx
   * @param gridy
   * @param insets
   */
  public GBC(int gridx, int gridy, Insets insets) {
    this(gridx, gridy, CENTER, NONE, insets);
  }

  /**
   * Construtor. Recebe as coordenadas (x, y); os demais valores permanecem com
   * os defaults:
   * 
   * <pre>
   * anchor = CENTER
   * fill = NONE
   * insets = (0, 0, 0, 0)
   * </pre>
   * 
   * @param gridx
   * @param gridy
   */
  public GBC(int gridx, int gridy) {
    this(gridx, gridy, CENTER, NONE, null);
  }

  /**
   * Construtor. Assume todos os valores default, sendo eles:
   * 
   * <pre>
   * gridx = 0
   * gridy = 0
   * anchor = CENTER
   * fill = NONE
   * insets = (0, 0, 0, 0)
   * </pre>
   */
  public GBC() {
    this(0, 0);
  }

  /**
   * Cria um GBC a partir de outro. Todas as propriedades sóo copiadas.
   * 
   * @param other GBC de referóncia
   */
  public GBC(GBC other) {
    this(other.gridx, other.gridy, (Insets) other.insets.clone());
    anchor = other.anchor;
    fill = other.fill;
    gridwidth = other.gridwidth;
    gridheight = other.gridheight;
    weightx = other.weightx;
    weighty = other.weighty;
  }

  // /////////////////////////////////////////
  // /////////////////////////////////////////
  // fill:

  /**
   * Define fill. Atribui peso 1.0 nas direóóes correspondentes caso os pesos
   * possuam valor 0.0.
   * 
   * @param _fill
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   * @see #horizontal()
   * @see #horizontal(double)
   * @see #vertical()
   * @see #vertical(double)
   * @see #both()
   * @see #both(double, double)
   */
  private GBC fill(int _fill) {
    this.fill = _fill;

    if (fill == HORIZONTAL || fill == BOTH) {
      if (weightx == 0.0) {
        weightx = 1.0;
      }
    }

    if (fill == VERTICAL || fill == BOTH) {
      if (weighty == 0.0) {
        weighty = 1.0;
      }
    }

    return this;
  }

  /**
   * Define fill = NONE.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC none() {
    return fill(NONE);
  }

  /**
   * Define fill = BOTH, com pesos em X e em Y sendo '1.0'.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC both() {
    return fill(BOTH);
  }

  /**
   * Define fill = BOTH.
   * 
   * @param wx - peso em X <b>(deve ser maior ou igual a zero)</b>
   * @param wy - peso em Y <b>(deve ser maior ou igual a zero)</b>
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC both(double wx, double wy) {
    if (wy < 0.0 || wx < 0.0) {
      throw new IllegalArgumentException(
        "GBC: pesos devem ser maiores ou iguais a zero");
    }
    fill(BOTH);
    this.weightx = wx;
    this.weighty = wy;
    return this;
  }

  /**
   * Define fill = VERTICAL, com peso em Y sendo '1.0'.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC vertical() {
    return fill(VERTICAL);
  }

  /**
   * Define fill = VERTICAL
   * 
   * @param wy - peso em Y <b>(deve ser maior ou igual a zero)</b>
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC vertical(double wy) {
    if (wy < 0.0) {
      throw new IllegalArgumentException(
        "GBC: peso deve ser maior ou igual a zero");
    }
    fill(VERTICAL);
    this.weighty = wy;
    return this;
  }

  /**
   * Define fill = HORIZONTAL, com peso em X sendo '1.0'.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC horizontal() {
    return fill(HORIZONTAL);
  }

  /**
   * Define fill = HORIZONTAL.
   * 
   * @param wx - peso em X <b>(deve ser maior ou igual a zero)</b>
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC horizontal(double wx) {
    if (wx < 0.0) {
      throw new IllegalArgumentException(
        "GBC: peso deve ser maior ou igual a zero");
    }
    fill(HORIZONTAL);
    weightx = wx;
    return this;
  }

  // /////////////////////////////////////////
  // /////////////////////////////////////////
  // óncoras

  /**
   * Define óncora.
   * 
   * @param _anchor
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  private GBC anchor(int _anchor) {
    this.anchor = _anchor;
    return this;
  }

  /**
   * Define anchor = CENTER.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC center() {
    return anchor(CENTER);
  }

  /**
   * Define anchor = NORTH.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC north() {
    return anchor(NORTH);
  }

  /**
   * Define anchor = NORTHEAST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC northeast() {
    return anchor(NORTHEAST);
  }

  /**
   * Define anchor = EAST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC east() {
    return anchor(EAST);
  }

  /**
   * Define anchor = SOUTHEAST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC southeast() {
    return anchor(SOUTHEAST);
  }

  /**
   * Define anchor = SOUTH.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC south() {
    return anchor(SOUTH);
  }

  /**
   * Define anchor = SOUTHWEST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC southwest() {
    return anchor(SOUTHWEST);
  }

  /**
   * Define anchor = WEST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC west() {
    return anchor(WEST);
  }

  /**
   * Define anchor = NORTHWEST.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC northwest() {
    return anchor(NORTHWEST);
  }

  /**
   * Associa o peso default (1.0) a ambas as direóóes, definindo
   * <code>fill == NONE</code>,
   * 
   * @see #pushxy(double, double)
   * @see #none()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushxy() {
    return pushxy(1.0, 1.0);
  }

  /**
   * Associa o peso default (1.0) ó direóóo X, anulando a expansóo nesta
   * direóóo.
   * 
   * @see #pushx(double)
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushx() {
    return pushx(1.0);
  }

  /**
   * Associa o peso default (1.0) ó direóóo Y, anulando a expansóo nesta
   * direóóo.
   * 
   * @see #pushy()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushy() {
    return pushy(1.0);
  }

  /**
   * Associa pesos a ambas as direóóes, definindo <code>fill == NONE</code>.
   * 
   * @param wx - peso no eixo X
   * @param wy - peso no eixo Y
   * 
   * @see #pushxy()
   * @see #none()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushxy(double wx, double wy) {
    return none().weights(wx, wy);
  }

  /**
   * Associa um peso ó direóóo X, anulando a expansóo nesta direóóo.
   * 
   * @param wx - peso X
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushx(double wx) {
    switch (fill) {
      case BOTH:
        vertical();
        break;

      case HORIZONTAL:
        none();
        break;
    }
    return weightx(wx);
  }

  /**
   * Associa um peso ó direóóo Y, anulando a expansóo nesta direóóo.
   * 
   * @param wy - peso Y
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC pushy(double wy) {
    switch (fill) {
      case BOTH:
        horizontal();
        break;

      case VERTICAL:
        none();
        break;
    }
    return weighty(wy);
  }

  /**
   * Faz com que o elemento aproveite espaóo horizontal disponóvel mas nóo
   * compita com os demais na distribuióóo de espaóo
   * <code>(weightx == 0.0)</code>.
   * <p>
   * O fill horizontal seró adicionado ao fill existente da seguinte forma:
   * 
   * <ul>
   * <li>none --&gt; horizontal
   * <li>horizontal --&gt; horizontal
   * <li>vertical --&gt; both
   * <li>both --&gt; both
   * </ul>
   * 
   * @see #filly()
   * @see #fillxy()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC fillx() {
    switch (fill) {
      case NONE:
        return horizontal(0.0);

      case HORIZONTAL:
      case BOTH:
        return weightx(0.0);

      case VERTICAL:
        return both(0.0, weighty);

      default:
        // nunca vai ser chamado, todos os casos estóo cobertos
        return this;
    }
  }

  /**
   * Faz com que o elemento aproveite espaóo vertical disponóvel mas nóo compita
   * com os demais na distribuióóo de espaóo <code>(weighty == 0.0)</code>.
   * <p>
   * O fill vertical seró adicionado ao fill existente da seguinte forma:
   * 
   * <ul>
   * <li>none --&gt; vertical
   * <li>horizontal --&gt; both
   * <li>vertical --&gt; vertical
   * <li>both --&gt; both
   * </ul>
   * 
   * @see #fillx()
   * @see #fillxy()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC filly() {
    switch (fill) {
      case NONE:
        return vertical(0.0);

      case VERTICAL:
      case BOTH:
        return weighty(0.0);

      case HORIZONTAL:
        return both(weightx, 0.0);

      default:
        // nunca vai ser chamado, todos os casos estóo cobertos
        return this;
    }
  }

  /**
   * Faz com que o elemento aproveite espaóo disponóvel em ambas as direóóes mas
   * nóo compita com os demais na distribuióóo de espaóo.
   * 
   * Equivale a <code>both(0.0, 0.0)</code>.
   * 
   * @see #fillx()
   * @see #filly()
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC fillxy() {
    return both(0.0, 0.0);
  }

  // /////////////////////////////////////////
  // /////////////////////////////////////////
  // insets

  /**
   * Define insets [margem entre o componente e sua 'cólula' no grid].
   * 
   * @param _insets - elemento do tipo {@link Insets}
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC insets(Insets _insets) {
    this.insets = _insets == null ? new Insets(0, 0, 0, 0) : _insets;
    return this;
  }

  /**
   * Define insets [margem entre o componente e sua 'cólula' no grid].
   * 
   * @param top
   * @param left
   * @param bottom
   * @param right
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC insets(int top, int left, int bottom, int right) {
    insets = new Insets(top, left, bottom, right);
    return this;
  }

  /**
   * Define insets [margem entre o componente e sua 'cólula' no grid]. O valor
   * indicado ó usado nos 4 cantos [top, left, bottom e right].
   * 
   * @param defaultValue - valor ónico aplicado em top, left, bottom e right.
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC insets(int defaultValue) {
    insets = new Insets(defaultValue, defaultValue, defaultValue, defaultValue);
    return this;
  }

  /**
   * Define insets.top = top.
   * 
   * @param top
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC top(int top) {
    insets.top = top;
    return this;
  }

  /**
   * Define insets.bottom = bottom.
   * 
   * @param bottom
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC bottom(int bottom) {
    insets.bottom = bottom;
    return this;
  }

  /**
   * Define insets.right = right.
   * 
   * @param right
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC right(int right) {
    insets.right = right;
    return this;
  }

  /**
   * Define insets.left = left.
   * 
   * @param left
   * 
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC left(int left) {
    insets.left = left;
    return this;
  }

  // /////////////////////////////////////////
  // /////////////////////////////////////////
  // pesos

  /**
   * Define o peso na direóóo horizontal.
   * 
   * @param wx - peso
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC weightx(double wx) {
    weightx = wx;
    return this;
  }

  /**
   * Define o peso na direóóo vertical.
   * 
   * @param wy - peso
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC weighty(double wy) {
    weighty = wy;
    return this;
  }

  /**
   * Define os pesos nas direóóes horizontal e vertical.
   * 
   * @param wx - peso na direóóo horizontal
   * @param wy - peso na direóóo vertical
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC weights(double wx, double wy) {
    weightx = wx;
    weighty = wy;
    return this;
  }

  // /////////////////////////////////////////
  // /////////////////////////////////////////
  // altura/largura [em cólulas, nóo pixels]

  /**
   * Define a largura (<code>gridwidth</code>).
   * 
   * @param width
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC width(int width) {
    gridwidth = width;
    return this;
  }

  /**
   * Define a altura (<code>gridheight</code>).
   * 
   * @param height
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC height(int height) {
    gridheight = height;
    return this;
  }

  /**
   * Define a coordenada X no grid.
   * 
   * @param gx - coordenada X no grid
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC gridx(int gx) {
    gridx = gx;
    return this;
  }

  /**
   * Define a coordenada Y no grid.
   * 
   * @param gy - coordenada Y no grid
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC gridy(int gy) {
    gridy = gy;
    return this;
  }

  /**
   * Define quantas cólulas o elemento ocuparó na horizontal.
   * 
   * @param gw - nómero de cólulas ocupadas pelo componente na horizontal
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC gridwidth(int gw) {
    gridwidth = gw;
    return this;
  }

  /**
   * Define quantas cólulas o elemento ocuparó na vertical.
   * 
   * @param gh - nómero de cólulas ocupadas pelo componente na vertical
   * @return o próprio elemento (para permitir concatenaóóo de operaóóes)
   */
  public GBC gridheight(int gh) {
    gridheight = gh;
    return this;
  }

  // /////////////
  // outros

  /**
   * Montagem de um texto depurativo com base em uma óncora.
   * 
   * @param value o valor de uma óncora.
   * @return o texto associado.
   */
  private static String getAnchorName(int value) {
    if (value == NORTHEAST) {
      return "NORTHEAST";
    }
    if (value == EAST) {
      return "EAST";
    }
    if (value == SOUTHEAST) {
      return "SOUTHEAST";
    }
    if (value == SOUTH) {
      return "SOUTH";
    }
    if (value == SOUTHWEST) {
      return "SOUTHWEST";
    }
    if (value == WEST) {
      return "WEST";
    }
    if (value == NORTHWEST) {
      return "NORTHWEST";
    }
    if (value == CENTER) {
      return "CENTER";
    }
    return Integer.toString(value);
  }

  /**
   * Montagem de um texto depurativo com base em um tipos di preenchimento
   * (fill).
   * 
   * @param value o valor de um fill.
   * @return o texto associado.
   */
  private static String getFillName(int value) {
    if (value == NONE) {
      return "NONE";
    }
    if (value == HORIZONTAL) {
      return "HORIZONTAL";
    }
    if (value == VERTICAL) {
      return "VERTICAL";
    }
    if (value == BOTH) {
      return "BOTH";
    }
    if (value == NORTH) {
      return "NORTH";
    }
    return Integer.toString(value);
  }

  /**
   * Texto depurativo associado ao objeto <code>GBC</code> com seus atributos
   * internos e parametrizaóóes.
   * 
   * @return um texto depurativo.
   */
  @Override
  public String toString() {
    return String
      .format(
        "[GBC] x=%d y=%d width=%d height=%d weightx=%.1f weighty=%.1f anchor=%s fill=%s insets=%s",
        gridx, gridy, gridwidth, gridheight, weightx, weighty,
        getAnchorName(anchor), getFillName(fill), insets.toString());
  }
}