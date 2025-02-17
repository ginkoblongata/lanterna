/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna;

import java.util.Objects;

/**
 * This class is structured in a way to allow for ease of use with static import or without static import.
 *
 * @author ginkoblongata
 */
public class TerminalMath {
    /*
    @FunctionalInterface
    static interface OneElse { Q ifOneElse(Q fallback, Q[] qs, AtLeastTwo next); }
    @FunctionalInterface
    static interface AtLeastTwo { Q whenAtLeastTwo(Q initial, Q[] qs, int initialIndex, BiFunction<Q,Q,Q> fromTwoDecideOne); }

    public static TerminalPosition min(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.min(q1)))));
    }
    public static TerminalPosition max(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.max(q1)))));
    }
    public static TerminalPosition add(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.add(q1)))));
    }
    public static TerminalPosition minus(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.minus(q1)))));
    }
    public static TerminalPosition mult(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.mult(q1)))));
    }
    public static TerminalPosition div(TerminalPosition...positions) {
        return asPosition(zeroElse(null, qs(positions),
            qs -> oneElse(null, qs,
                qs -> twoOrMore(first(qs), qs, 1, (q0, q1) -> q0.div(q1)))));
    }
    

    private static Q zeroElse(Q base, Q[] qs, Function<Q[], Q> next) {
        return qs == null || qs.length == 0 ? base : next.accept(qs);
    }
    private static Q oneElse(Q base, Q[] qs, Function<Q[], Q> next) {
        return qs != null && qs.length == 1 ? base : next.ifOneElse(qs);
    }
    private static Q twoOrMore(Q base, Q[] qs, int initialIndex, BiFunction<Q, Q, Q> bifunction) {
        Q result = base;
        if (qs == null || qs.length <2) {
            return result;
        }
        for (int i = initialIndex; i < qs.length; i++) {
            result = bifunction(result, qs[i]);
        }
        return result;
    }
    


    private final static Q[] noq = new Q[0];
    private static TerminalPosition nullSafe(TerminalPosition...positions, TerminalPosition nullSafe, Supplier<TerminalPosition> supplier) {
        return positions == null ? nullSafe : supplier.get();
    }
    private static TerminalPosition asPosition(Q q) { return q == null ? null : q.asPosition(); }
    private static TerminalSize asSize(Q q) { return q == null ? null : q.asSize(); }
    
    private static Q[] emptyqs(TerminalPosition... items) { items == null ? noq : new Q[items.length]; }
    
    private static Q[] fill(Q[] qs, Function<Integer, Q> conversion) {
        for (int i = qs.length -1; i >= 0; i--) {
            qs[i] = conversion.accept(i);
        }
        return qs;
    }
    private static Q[] qs(TerminalPosition... items) { return fill(emptyqs(items), i -> Q.of(items[i])); }
    private static Q[] qs(TerminalSize... items)     { return fill(emptyqs(items), i -> Q.of(items[i])); }
    
    
    private static Q first(Q[] qs) {
        if (positions != null && positions.length > 0) {
            return positions[0];
        }
        return null;
    }
    
    static class Q {
        private Q(int d0, int d1) { this.d0 = d0; this.d1 = d1; }
        final int d0;
        final int d1;
        
        public static Q of()                    { return new Q(0, 0); }
        public static Q of(TerminalPosition tp) { return new Q(tp.getColumn(), tp.getRow()); }
        public static Q of(TerminalSize ts)     { return new Q(ts.getColumns(), ts.getRows()); }
        public static Q of(int d0, int d1)      { return new Q(d0, d1); }
        public static Q of(int z)               { return new Q(z, z); }
        public static Q of(boolean b)           { return new Q(b ? 1 : 0, b ? 0 : 1); }
        
        
        public Q r90() { return of(d1, d0); }
        
        public TerminalSize asSize()         { return TerminalSize.of(d0, d1); }
        public TerminalPosition asPosition() { return TerminalPosition.of(d0, d1); }
        
        public Q min(Q other)   { return of(Math.min(other.d0, d0), Math.min(other.d1, d1)); }
        public Q min(int d0, d1){ return min(Q.of(d0, d1)); }
        public Q min(int z)     { return min(Q.of(z)); }
        
        public Q max(Q other)   { return of(Math.max(other.d0, d0), Math.max(other.d1, d1)); }
        public Q max(int d0, d1){ return max(Q.of(d0, d1)); }
        public Q max(int z)     { return max(Q.of(z)); }
        
        public Q plus(Q other)  { return of(d0 + other.d0, d1 + other.d1); }
        public Q plus(int z)    { return of(d0 + z       , d1 + z       ); }
        public Q minus(Q other) { return of(d0 - other.d0, d1 - other.d1); }
        public Q minus(int z)   { return of(d0 - z       , d1 - z       ); }
        
        public Q mult(Q other)  { return of(other.d0 * d0, other.d1 * d1); }
        public Q mult(int z)    { return of(d0 * z       , d1 * z       ); }
        public Q mult(double z) { return of((int)(d0 * z), (int)(d1 * z)); }
        public Q div(Q other)   { return of(d0 / other.d0, d1 / other.d1); }
        public Q div(double z)  { return of((int)(d0 / z), (int)(d1 / z)); }
    }
    */
    
}

