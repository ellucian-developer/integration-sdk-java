/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.config;


import java.util.Objects;

/**
 * The SemVer class holds the semantic version for an Ethos resource.  Implements the Comparable interface to enable
 * easy sorting of this class by major/minor/patch values.
 * <p>
 * NOTE: Not all versions of Ethos resources are Semantic versions.  Therefore, this class should only be used in support of
 * those versions of resources following Semantic version notation.
 */
public class SemVer implements Comparable<SemVer> {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /**
     * The major version of this SemVer.
     */
    private int major;

    /**
     * The minor version of this SemVer.
     */
    private int minor;

    /**
     * The patch version of this SemVer.
     */
    private int patch;

    /**
     * An inner static Builder class used for building the SemVer object with various criteria.  This uses the builder
     * fluent API pattern.
     * All of the attributes in this Builder class correspond to the attributes in the containing SemVer class.
     */
    public static class Builder {
        /**
         * The major value to build the SemVer with.
         */
        private int major;

        /**
         * The minor value to build the SemVer with.
         */
        private int minor;

        /**
         * The patch value to build the SemVer with.
         */
        private int patch;

        /**
         * No-arg constructor for the Builder.  Sets all the version values to 0.
         */
        public Builder() {
            this.major = 0;
            this.minor = 0;
            this.patch = 0;
        }

        /**
         * A Builder constructor that takes a version string which should have a value in SemVer notation,
         * e.g. 12.0.1, or 11, or 9.2.  Attempts to parse the version into the appropriate major, minor, and/or patch values.
         * @param version The version value in SemVer notation to parse into the major, minor, and/or patch values.
         */
        public Builder( String version ) {
            this();
            if( version == null || version.isBlank() ) {
                return;
            }
            if( version.startsWith("v") ) {
                version = version.substring( 1 );
            }
            if( version.contains(".") == false ) {
                this.major = Integer.valueOf( version );
            }
            else {
                this.major = Integer.valueOf( version.substring(0, version.indexOf(".")) );
                version = version.substring( version.indexOf(".") + 1 );
                if( version.contains(".") == false ) {
                    this.minor = Integer.valueOf( version );
                }
                else {
                    this.minor = Integer.valueOf( version.substring(0, version.indexOf(".")) );
                    version = version.substring( version.indexOf(".") + 1 );
                    if( version.isBlank() == false ) {
                        this.patch = Integer.valueOf( version );
                    }
                }
            }
        }

        /**
         * Sets the given major value for the version in the builder.
         * @param major The major value to set.
         * @return This builder instance with the major value set.
         */
        public Builder withMajor( int major ) {
            this.major = major;
            return this;
        }

        /**
         * Sets the given minor value for the version in the builder.
         * @param minor The minor value to set.
         * @return This builder instance with the minor value set.
         */
        public Builder withMinor( int minor ) {
            this.minor = minor;
            return this;
        }

        /**
         * Sets the given patch value for the version in the builder.
         * @param patch The patch value to set.
         * @return This builder instance with the patch value set.
         */
        public Builder withPatch( int patch ) {
            this.patch = patch;
            return this;
        }

        /**
         * Builds a SemVer object with the given major, minor, and patch values that have been set in the builder.
         * @return A SemVer object containing the major, minor, and patch values from the builder.
         */
        public SemVer build() {
            SemVer semVer = new SemVer();
            semVer.major = this.major;
            semVer.minor = this.minor;
            semVer.patch = this.patch;
            return semVer;
        }

    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * Gets the major version value.
     * @return The major version value.
     */
    public int getMajor() {
        return major;
    }


    /**
     * Gets the minor version value.
     * @return The minor version value.
     */
    public int getMinor() {
        return minor;
    }


    /**
     * Gets the patch version value.
     * @return The patch version value.
     */
    public int getPatch() {
        return patch;
    }


    /**
     * Compares this object to the given object, which should also be an instance of this class.
     * Compares by the major, then minor, then patch values.
     * Returns the difference between the major, minor, and patch values.  If the difference of the major values is 0,
     * then takes the difference of the minor values.  If the difference of the minor values is also 0, then it returns
     * the difference of the patch values.
     * @param sv Another SemVer instance to compare against this one.
     * @return A negative value if this SemVer is less than the given SemVer object, a positive value if this SemVer is greater
     *         than the given SemVer object, or 0 if they are equal.
     */
    @Override
    public int compareTo(SemVer sv) {
        if( sv == null ) {
            return 1; // This is greater than a null object, so if sv is null return a positive 1.
        }
        int result = this.major - sv.major;
        if( result == 0 ) {
            result = this.minor - sv.minor;
            if( result == 0 ) {
                result = this.patch - sv.patch;
            }
        }
        return result;
    }

    /**
     * Overrides the equals method to compare on the major, minor, and patch values.
     * @param o The object to compare equality against, expected to be another instance of this class.
     * @return true if the given SemVer object has the same major, minor, and patch values as this class, false otherwise.
     */
    @Override
    public boolean equals( Object o) {
        if( (o instanceof SemVer) == false ) {
            return false;
        }
        if( this == o ) {
            return true;
        }
        SemVer semVer = (SemVer) o;
        if (this.major == semVer.major &&
            this.minor == semVer.minor &&
            this.patch == semVer.patch) {
            return true;
        }
        return false;
    }

    /**
     * Overrides the hashCode to compute and return the hashCode value comprised of the major, minor, and patch values
     * of this class.
     * @return A hashCode computed from the major, minor, and patch values of this class.
     */
    @Override
    public int hashCode() {
        return Objects.hash( major, minor, patch );
    }

    /**
     * Returns a string representation of this object containing the SemVer notation of the major.minor.patch values.
     * @return a string representation of this object in SemVer notation.
     */
    public String toString() {
        return new StringBuilder().append(major).append(".").append(minor).append(".").append(patch).toString();
    }

}