import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.util.List;

import static org.testng.Assert.*;
import static org.openqa.selenium.Keys.RETURN;

public class TestTodolist {
    private WebDriver driver;
    private final String todolistName = "Todo_list_#1";
    private int countBefore, countAfter;
    
    // this method is used to count objects on pages by its sccSelectors, e.g. tolists and todos
    public int countElements(String sccSelectorValue) {
        List<WebElement> elementsArr = driver.findElements(By.cssSelector(sccSelectorValue));
        return elementsArr.size();
    }

    @BeforeSuite
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("https://eviltester.github.io/simpletodolist/todolists.html");
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void createList() {
        countBefore = countElements("ul.todo-list-list li");

        WebElement newTodoList = driver.findElement(By.cssSelector(".new-todo-list"));
        newTodoList.sendKeys(todolistName, RETURN);

        countAfter = countElements("ul.todo-list-list li");
        assertEquals(countAfter, countBefore + 1);
    }

    @Test(dependsOnMethods = "createList")
    public void openList() {
        WebElement todoList = driver.findElement(By.cssSelector("[data-id='" + todolistName + "'] a"));
        todoList.click();

        WebElement todoListTitle = driver.findElement(By.cssSelector("#todostitle"));
        String[] strings = todoListTitle.getText().split(" : ");
        String todolistNameActual = strings[1];

        assertEquals(todolistNameActual, todolistName);
    }

    @Test(dependsOnMethods = "openList")
    public void addTodo() {
        countBefore = countElements("ul.todo-list li");

        WebElement newTodo = driver.findElement(By.cssSelector(".new-todo"));
        newTodo.sendKeys("Todo", RETURN);

        countAfter = countElements("ul.todo-list li");
        assertEquals(countAfter, countBefore + 1);
    }

    @Test(dependsOnMethods = "addTodo")
    public void editTodo() {
        WebElement todoCheckbox = driver.findElement(By.xpath("//label[text()='Todo']/preceding-sibling::input[@class='toggle']"));
        todoCheckbox.click();
        assertEquals("true", todoCheckbox.getDomProperty("checked"));
    }

    @Test(dependsOnMethods = "editTodo")
    public void deleteTodo() {
        countBefore = countElements("ul.todo-list li");

        WebElement destroyTodoButton = driver.findElement(By.xpath("//label[text()='Todo']/following-sibling::button[@class='destroy']"));
        destroyTodoButton.click();

        countAfter = countElements("ul.todo-list li");
        assertEquals(countAfter, countBefore - 1);
    }

    @Test(dependsOnMethods = "deleteTodo")
    public void clickLists() {
        WebElement linkLists = driver.findElement(By.cssSelector("#navtodolists"));
        linkLists.click();

        WebElement listTitle = driver.findElement(By.cssSelector("h1"));
        assertEquals(listTitle.getText(), "todos List Management");
    }

    @Test(dependsOnMethods = "clickLists")
    public void deleteList() throws InterruptedException {
        countBefore = countElements(".todo-list-list li");

        WebElement destroyListButton = driver.findElement(By.xpath("//label[text()='Todo_list_#1']/following-sibling::button[@class='destroy']"));
        destroyListButton.click();

        //following code is used to accept list removing on alert modal
        Alert alert = driver.switchTo().alert();
        alert.accept();

        countAfter = countElements(".todo-list-list li");
        assertEquals(countAfter, countBefore - 1);
    }
}
