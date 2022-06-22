/*
 * ******************************************************************************
 *   Copyright 2022 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SemVerTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private final static Log log = LogFactory.getLog(SemVerTest.class);

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    public void semVerBuilderVersionTest() {
        SemVer semVer = new SemVer.Builder( null ).build();
        assert( semVer != null );
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("0.0.0") );
        semVer = new SemVer.Builder("").build();
        assert( semVer != null );
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("0.0.0") );

        Assertions.assertThrows(NumberFormatException.class, () -> {
            // Should throw a NumberFormatException since 'bob' is not numeric.
            SemVer sv = new SemVer.Builder("bob").build();
        });

        semVer = new SemVer.Builder("1").build();
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("1.0.0") );
        semVer = new SemVer.Builder("1.1").build();
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("1.1.0") );
        semVer = new SemVer.Builder("1.1.1").build();
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("1.1.1") );

        semVer = new SemVer.Builder("v1").build();
        assert( semVer.toString() != null );
        assert( semVer.toString().equals("1.0.0") );
    }

    @Test
    public void semVerWithMajorMinorPatchVersionsTest() {
        SemVer semVer = new SemVer.Builder().withMajor(1).build();
        assert( semVer.getMajor() == 1 );
        assert( semVer.getMinor() == 0 );
        assert( semVer.getPatch() == 0 );
        semVer = new SemVer.Builder().withMinor(1).build();
        assert( semVer.getMajor() == 0 );
        assert( semVer.getMinor() == 1 );
        assert( semVer.getPatch() == 0 );
        semVer = new SemVer.Builder().withPatch(1).build();
        assert( semVer.getMajor() == 0 );
        assert( semVer.getMinor() == 0 );
        assert( semVer.getPatch() == 1 );
        semVer = new SemVer.Builder().withMajor(1).withMinor(1).build();
        assert( semVer.getMajor() == 1 );
        assert( semVer.getMinor() == 1 );
        assert( semVer.getPatch() == 0 );
        semVer = new SemVer.Builder().withMajor(1).withMinor(1).withPatch(1).build();
        assert( semVer.getMajor() == 1 );
        assert( semVer.getMinor() == 1 );
        assert( semVer.getPatch() == 1 );
    }

    @Test
    public void semVerEqualsTest() {
        SemVer svA = new SemVer.Builder("1" ).build();
        SemVer svB = new SemVer.Builder("1.0" ).build();
        assert( svA.equals(svB) );
        svA = new SemVer.Builder("v1" ).build();
        svB = new SemVer.Builder("1.0" ).build();
        assert( svA.equals(svB) );
        svA = new SemVer.Builder("1.0" ).build();
        svB = new SemVer.Builder("1.0" ).build();
        assert( svA.equals(svB) );
        svA = new SemVer.Builder("1.0" ).build();
        svB = new SemVer.Builder("1.1" ).build();
        assert( svA.equals(svB) == false );
    }

    @Test
    public void semVerCompareSortTest() {
        List<SemVer> semVerList = new ArrayList<>();
        semVerList.add( new SemVer.Builder("1.0.0").build() );
        semVerList.add( new SemVer.Builder("1.1.1").build() );
        semVerList.add( new SemVer.Builder("1.1.0").build() );
        Collections.sort( semVerList );
        assert( semVerList.get(0).toString().equals("1.0.0") );
        Collections.reverse( semVerList );
        assert( semVerList.get(0).toString().equals("1.1.1") );
    }
}