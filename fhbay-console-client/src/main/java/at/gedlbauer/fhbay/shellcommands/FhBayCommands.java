package at.gedlbauer.fhbay.shellcommands;

import at.gedlbauer.fhbay.domain.Article;
import at.gedlbauer.fhbay.domain.Bid;
import at.gedlbauer.fhbay.domain.Customer;
import at.gedlbauer.fhbay.logic.ArticleService;
import at.gedlbauer.fhbay.logic.BidService;
import at.gedlbauer.fhbay.logic.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@ShellComponent
public class FhBayCommands {

    Customer loggedInCustomer = null;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BidService bidService;

    @ShellMethod("find all customers")
    public String getCustomers() {
        return customerService.findAll().toString();
    }

    @ShellMethod("find customer by id")
    public String customerById(@ShellOption Long id) {
        var customer = customerService.findById(id);
        return customer.isPresent() ? customer.get().toString() : "<null>";
    }

    @ShellMethod("select user")
    public String setUser(@ShellOption Long id) {
        var customer = customerService.findById(id);
        if (customer.isEmpty()) {
            return "Invalid user!";
        }
        loggedInCustomer = customer.get();
        return "Logged in as " + loggedInCustomer.getFirstName() + " " + loggedInCustomer.getLastName() + " (" +
                customer.toString() + ")";
    }

    @ShellMethod("get current user")
    public String getUser() {
        return loggedInCustomer != null ? loggedInCustomer.toString() : "<null>";
    }

//    private Long id;
//    private String name;
//    private String description;
//    private double startPrice;
//    private double endPrice;
//    private LocalDateTime auctionStart;
//    private LocalDateTime auctionEnd;

    @ShellMethod("insert or update article")
    public String saveArticle(@ShellOption String name,
                              @ShellOption String description,
                              @ShellOption(defaultValue = "0.0") double startPrice,
                              @ShellOption(defaultValue = "") String auctionEnd) {
        String warning = "";
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        if (!auctionEnd.isEmpty()) {
            try {
                endDate = LocalDateTime.parse(auctionEnd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ignored) {
                throw new IllegalStateException("invalid end date format - enter valid auctionEnd or leave empty to use current date + 7 days");
            }
        }
        var article = new Article(name, description, startPrice, 0.0, LocalDateTime.now(), endDate, loggedInCustomer, null);
        article = articleService.save(article);
        return "Inserted " + article.toString();
    }

    @ShellMethod("find all articles")
    public String getArticles() {
        return articleService.findAll().toString();
    }

    @ShellMethod("update seller of article")
    public String updateSeller(@ShellOption Long articleId, @ShellOption Long sellerId) {
        var newSeller = customerService.findById(sellerId).orElseThrow();
        var article = articleService.findById(articleId).orElseThrow();
        article = articleService.updateSeller(article, newSeller);
        return "Updated article - new article is: " + article.toString();
    }

    @ShellMethod("update buyer of article")
    public String updateBuyer(@ShellOption Long articleId, @ShellOption Long buyerId) {
        var newBuyer = customerService.findById(buyerId).orElseThrow();
        var article = articleService.findById(articleId).orElseThrow();
        article = articleService.updateBuyer(article, newBuyer);
        return "Updated article - new article is: " + article.toString();
    }

    @ShellMethod("get articles where name contains")
    public String getByName(@ShellOption String name) {
        return articleService.findByName(name).toString();
    }

    @ShellMethod("get articles where description contains")
    public String getByDescription(@ShellOption String description) {
        return articleService.findByDescription(description).toString();
    }

    @ShellMethod("add bid to article")
    public String addBid(Long articleId, double price) {
        var bid = new Bid(price, LocalDateTime.now(), loggedInCustomer, articleService.findById(articleId).orElseThrow());
        bid = bidService.save(bid);
        return "Inserted " + bid.toString();
    }
}
