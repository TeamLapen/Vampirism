package de.teamlapen.vampirism.api.components;

/**
 * Item Component to hold book content.
 */
public interface IVampireBookContent {

    /**
     * @return The book id
     */
    String id();

    /**
     * @return The book author
     */
    String author();

    /**
     * @return The book title
     */
    String title();
}
