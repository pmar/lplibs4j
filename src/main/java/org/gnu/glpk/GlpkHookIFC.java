//  GlpkHookIFC.java - Install user hooks for printing and faults

// ---------------------------------------------------------------------
// Copyright (C) 2005 Andrew Makhorin <mao@mai2.rcnet.ru>, Department
// for Applied Informatics, Moscow Aviation Institute, Moscow, Russia.
// All rights reserved.
//
// Author: Chris Rosebrugh, cpr@pobox.com
//
// This file is a part of GLPK (GNU Linear Programming Kit).
//
// GLPK is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GLPK is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with GLPK; see the file COPYING. If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.
// ---------------------------------------------------------------------

package org.gnu.glpk;

/**
 * Callback for handling printing and fault messages
 * @see org.gnu.glpk.GlpkSolver#setHook(org.gnu.glpk.GlpkHookIFC hook)
 */
public interface GlpkHookIFC {
    public void fault(String s);
    public void print(String s);
}