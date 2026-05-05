import java.util.*;

public interface IMenuManager {
    // no discprtion
    MenuItem add(String name, double priceEGP, Category category);

    // with description:
    MenuItem add(String name, String description, double priceEGP, Category category);

    Optional<MenuItem> findById(String id);


    //  updater (with description change):
    boolean updateById(String id, String newName, String newDescription, double newPriceEGP, Category newCategory);

    boolean removeById(String id);
    void displayMenu();


}
