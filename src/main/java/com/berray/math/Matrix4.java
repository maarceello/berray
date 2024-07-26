package com.berray.math;

/**
 * 4x4 Matrix in row major order.
 */
public class Matrix4 {
  private float a;
  private float b;
  private float c;
  private float d;
  private float e;
  private float f;
  private float g;
  private float h;
  private float i;
  private float j;
  private float k;
  private float l;
  private float m;
  private float n;
  private float o;
  private float p;

  public Matrix4() {

  }

  public Matrix4(float a, float b, float c, float d, float e, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
    this.g = g;
    this.h = h;
    this.i = i;
    this.j = j;
    this.k = k;
    this.l = l;
    this.m = m;
    this.n = n;
    this.o = o;
    this.p = p;
  }

  public static Matrix4 identity() {
    Matrix4 matrix4 = new Matrix4();
    matrix4.a = 1.0f;
    matrix4.f = 1.0f;
    matrix4.k = 1.0f;
    matrix4.p = 1.0f;
    return matrix4;
  }

  public Matrix4(Matrix4 other) {
    this.a = other.a;
    this.b = other.b;
    this.c = other.c;
    this.d = other.d;
    this.e = other.e;
    this.f = other.f;
    this.g = other.g;
    this.h = other.h;
    this.i = other.i;
    this.j = other.j;
    this.k = other.k;
    this.l = other.l;
    this.m = other.m;
    this.n = other.n;
    this.o = other.o;
    this.p = other.p;
  }

  /**
   * Multiply the current matrix with other and returns the result.
   */
  public Matrix4 multiply(Matrix4 other) {
    float Aa = this.a;
    float Ab = this.b;
    float Ac = this.c;
    float Ad = this.d;
    float Ae = this.e;
    float Af = this.f;
    float Ag = this.g;
    float Ah = this.h;
    float Ai = this.i;
    float Aj = this.j;
    float Ak = this.k;
    float Al = this.l;
    float Am = this.m;
    float An = this.n;
    float Ao = this.o;
    float Ap = this.p;
    float Ba = other.a;
    float Bb = other.b;
    float Bc = other.c;
    float Bd = other.d;
    float Be = other.e;
    float Bf = other.f;
    float Bg = other.g;
    float Bh = other.h;
    float Bi = other.i;
    float Bj = other.j;
    float Bk = other.k;
    float Bl = other.l;
    float Bm = other.m;
    float Bn = other.n;
    float Bo = other.o;
    float Bp = other.p;
    Matrix4 C = new Matrix4();
    C.a = Aa * Ba + Ab * Be + Ac * Bi + Ad * Bm;
    C.b = Aa * Bb + Ab * Bf + Ac * Bj + Ad * Bn;
    C.c = Aa * Bc + Ab * Bg + Ac * Bk + Ad * Bo;
    C.d = Aa * Bd + Ab * Bh + Ac * Bl + Ad * Bp;
    C.e = Ae * Ba + Af * Be + Ag * Bi + Ah * Bm;
    C.f = Ae * Bb + Af * Bf + Ag * Bj + Ah * Bn;
    C.g = Ae * Bc + Af * Bg + Ag * Bk + Ah * Bo;
    C.h = Ae * Bd + Af * Bh + Ag * Bl + Ah * Bp;
    C.i = Ai * Ba + Aj * Be + Ak * Bi + Al * Bm;
    C.j = Ai * Bb + Aj * Bf + Ak * Bj + Al * Bn;
    C.k = Ai * Bc + Aj * Bg + Ak * Bk + Al * Bo;
    C.l = Ai * Bd + Aj * Bh + Ak * Bl + Al * Bp;
    C.m = Am * Ba + An * Be + Ao * Bi + Ap * Bm;
    C.n = Am * Bb + An * Bf + Ao * Bj + Ap * Bn;
    C.o = Am * Bc + An * Bg + Ao * Bk + Ap * Bo;
    C.p = Am * Bd + An * Bh + Ao * Bl + Ap * Bp;
    return C;
  }

  /**
   * Multiply the current matrix with a vector and returns the result.
   */
  public Vec3 multiply(Vec3 other) {
    return multiply(other.x, other.y, other.z);
  }

  /**
   * Multiply the current matrix with a vector and returns the result.
   */
  public Vec3 multiply(float x, float y, float z) {
    Matrix4 a = this;
    float x1 = a.a * x + a.b * y + a.c * z + a.d/* * w*/;
    float y1 = a.e * x + a.f * y + a.g * z + a.h/* * w*/;
    float z1 = a.i * x + a.j * y + a.k * z + a.l/* * w*/;
//    result.w = a.m * x + a.n * y + a.o * z + a.p/* * w*/;
    return new Vec3(x1,y1,z1);
  }

  public Matrix4 scale(float x, float y, float z) {
    return multiply(Matrix4.fromScale(x, y, z));
  }

  public Matrix4 translate(float x, float y, float z) {
    return multiply(Matrix4.fromTranslate(x, y, z));
  }

  public Matrix4 rotatex(float angle) {
    return multiply(Matrix4.fromRotatex(angle));
  }

  public Matrix4 rotatey(float angle) {
    return multiply(Matrix4.fromRotatey(angle));
  }

  public Matrix4 rotatez(float angle) {
    return multiply(Matrix4.fromRotatez(angle));
  }

  public Matrix4 rotateAxis(float angle, Vec3 axis) {
    return multiply(Matrix4.fromRotateAxis(angle, axis));
  }

  public Matrix4 rotateEuler(float heading, float attitude, float bank) {
    return multiply(Matrix4.fromRotateEuler(heading, attitude, bank));
  }

  public Matrix4 transpose() {
    return new Matrix4(this.a, this.e, this.i, this.m,
        this.b, this.f, this.j, this.n,
        this.c, this.g, this.k, this.o,
        this.d, this.h, this.l, this.p);
  }

  public static Matrix4 fromScale(float x, float y, float z) {
    Matrix4 m = Matrix4.identity();
    m.a = x;
    m.f = y;
    m.k = z;
    return m;
  }

  public static Matrix4 fromTranslate(float x, float y, float z) {
    Matrix4 m = Matrix4.identity();
    m.d = x;
    m.h = y;
    m.l = z;
    return m;
  }

  public static Matrix4 fromRotatex(float angle) {
    Matrix4 m = Matrix4.identity();
    float sin = (float) Math.sin(angle);
    float cos = (float) Math.cos(angle);
    m.f = cos;
    m.k = cos;
    m.g = -sin;
    m.j = sin;
    return m;
  }

  public static Matrix4 fromRotatey(float angle) {
    Matrix4 m = Matrix4.identity();
    float sin = (float) Math.sin(angle);
    float cos = (float) Math.cos(angle);
    m.a = cos;
    m.k = cos;
    m.c = sin;
    m.i = -sin;
    return m;
  }

  public static Matrix4 fromRotatez(float angle) {
    Matrix4 m = Matrix4.identity();
    float sin = (float) Math.sin(angle);
    float cos = (float) Math.cos(angle);
    m.a = cos;
    m.f = cos;
    m.b = -sin;
    m.e = sin;
    return m;
  }

  public static Matrix4 fromRotateAxis(float angle, Vec3 axis) {
    Vec3 vector = axis.normalize();
    float x = vector.x;
    float y = vector.y;
    float z = vector.z;

    Matrix4 m = Matrix4.identity();
    float sin = (float) Math.sin(angle);
    float cos = (float) Math.cos(angle);
    float c1 = 1.0f - cos;

    m.a = x * x * c1 + cos;
    m.b = x * y * c1 - z * sin;
    m.c = x * z * c1 + y * sin;
    m.e = y * x * c1 + z * sin;
    m.f = y * y * c1 + cos;
    m.g = y * z * c1 - x * sin;
    m.i = x * z * c1 - y * sin;
    m.j = y * z * c1 + x * sin;
    m.k = z * z * c1 + cos;
    return m;
  }

  public static Matrix4 fromRotateEuler(float heading, float attitude, float bank) {
    // from http://www.euclideanspace.com/
    float ch = (float) Math.cos(heading);
    float sh = (float) Math.sin(heading);
    float ca = (float) Math.cos(attitude);
    float sa = (float) Math.sin(attitude);
    float cb = (float) Math.cos(bank);
    float sb = (float) Math.sin(bank);

    Matrix4 m = Matrix4.identity();
    m.a = ch * ca;
    m.b = sh * sb - ch * sa * cb;
    m.c = ch * sa * sb + sh * cb;
    m.e = sa;
    m.f = ca * cb;
    m.g = -ca * sb;
    m.i = -sh * ca;
    m.j = sh * sa * cb + ch * sb;
    m.k = -sh * sa * sb + ch * cb;
    return m;
  }

  public float determinant() {
    return ((this.a * this.f - this.e * this.b)
        * (this.k * this.p - this.o * this.l)
        - (this.a * this.j - this.i * this.b)
        * (this.g * this.p - this.o * this.h)
        + (this.a * this.n - this.m * this.b)
        * (this.g * this.l - this.k * this.h)
        + (this.e * this.j - this.i * this.f)
        * (this.c * this.p - this.o * this.d)
        - (this.e * this.n - this.m * this.f)
        * (this.c * this.l - this.k * this.d)
        + (this.i * this.n - this.m * this.j)
        * (this.c * this.h - this.g * this.d));
  }

  public Matrix4 inverse() {
    Matrix4 m = Matrix4.identity();
    Matrix4 self = this;
    float d = self.determinant();

    if (Math.abs(d) < 0.001) {
      // No inverse, return identity
      return m;
    }

    d = 1.0f / d;

    m.a = d * (self.f * (self.k * self.p - self.o * self.l) + self.j * (self.o * self.h - self.g * self.p) + self.n * (self.g * self.l - self.k * self.h));
    m.e = d * (self.g * (self.i * self.p - self.m * self.l) + self.k * (self.m * self.h - self.e * self.p) + self.o * (self.e * self.l - self.i * self.h));
    m.i = d * (self.h * (self.i * self.n - self.m * self.j) + self.l * (self.m * self.f - self.e * self.n) + self.p * (self.e * self.j - self.i * self.f));
    m.m = d * (self.e * (self.n * self.k - self.j * self.o) + self.i * (self.f * self.o - self.n * self.g) + self.m * (self.j * self.g - self.f * self.k));

    m.b = d * (self.j * (self.c * self.p - self.o * self.d) + self.n * (self.k * self.d - self.c * self.l) + self.b * (self.o * self.l - self.k * self.p));
    m.f = d * (self.k * (self.a * self.p - self.m * self.d) + self.o * (self.i * self.d - self.a * self.l) + self.c * (self.m * self.l - self.i * self.p));
    m.j = d * (self.l * (self.a * self.n - self.m * self.b) + self.p * (self.i * self.b - self.a * self.j) + self.d * (self.m * self.j - self.i * self.n));
    m.n = d * (self.i * (self.n * self.c - self.b * self.o) + self.m * (self.b * self.k - self.j * self.c) + self.a * (self.j * self.o - self.n * self.k));

    m.c = d * (self.n * (self.c * self.h - self.g * self.d) + self.b * (self.g * self.p - self.o * self.h) + self.f * (self.o * self.d - self.c * self.p));
    m.g = d * (self.o * (self.a * self.h - self.e * self.d) + self.c * (self.e * self.p - self.m * self.h) + self.g * (self.m * self.d - self.a * self.p));
    m.k = d * (self.p * (self.a * self.f - self.e * self.b) + self.d * (self.e * self.n - self.m * self.f) + self.h * (self.m * self.b - self.a * self.n));
    m.o = d * (self.m * (self.f * self.c - self.b * self.g) + self.a * (self.n * self.g - self.f * self.o) + self.e * (self.b * self.o - self.n * self.c));

    m.d = d * (self.b * (self.k * self.h - self.g * self.l) + self.f * (self.c * self.l - self.k * self.d) + self.j * (self.g * self.d - self.c * self.h));
    m.h = d * (self.c * (self.i * self.h - self.e * self.l) + self.g * (self.a * self.l - self.i * self.d) + self.k * (self.e * self.d - self.a * self.h));
    m.l = d * (self.d * (self.i * self.f - self.e * self.j) + self.h * (self.a * self.j - self.i * self.b) + self.l * (self.e * self.b - self.a * self.f));
    m.p = d * (self.a * (self.f * self.k - self.j * self.g) + self.e * (self.j * self.c - self.b * self.k) + self.i * (self.b * self.g - self.f * self.c));

    return m;
  }

  public float[] toFloat() {
    return new float[] {
        a,b,c,d,
        e,f,g,h,
        i,j,k,l,
        m,n,o,p
    };
  }
  public float[] toFloatTransposed() {
    return new float[] {
        a,e,i,m,
        b,f,j,n,
        c,g,k,o,
        d,h,l,p
    };
  }

  @Override
  public String toString() {
    return String.format("| %4.3f %4.3f %4.3f %4.3f |%n", a, b, c, d) +
        String.format("| %4.3f %4.3f %4.3f %4.3f |%n", e, f, g, h) +
        String.format("| %4.3f %4.3f %4.3f %4.3f |%n", i, j, k, l) +
        String.format("| %4.3f %4.3f %4.3f %4.3f |%n", m, n, o, p);
  }
}
