package de.teamlapen.sync.properties;

/**
 * Property accessor to allow transitive or deferred property management
 */
public interface IProperty {

    /**
     * The current unique status of the property
     * @implSpec should return the {@link Object#hashCode()} of the property values
     */
    int getStatus();

    /**
     * @return {@code true} if the property is allowed be synced to the client, otherwise {@code false}
     */
    boolean hasClientSync();

    /**
     * @return {@code true} if the property is allowed be saved to disk, otherwise {@code false}
     */
    boolean hasServerLoad();

}
