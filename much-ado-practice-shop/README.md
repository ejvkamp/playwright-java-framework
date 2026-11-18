<!-- SHOP MIGRATION (2026-11-18): new folder; this README and the LICENSE are the shop's own (MIT), download link points at this repo's release -->
# Much Ado To You: the practice shop

A small online shop for practicing test automation, built for readers of *Much Ado About Automation: A Playwright Guide for Java*.
It runs on your own computer, so it never changes underneath the book, never goes offline, and you can break it as often as you like.

Everything is **fake**: there are no real payments, no shipping and no real accounts. Nothing leaves your machine, and the shop
only listens on `localhost`, so other computers cannot reach it.

## Run it

You need **Java 17 or newer**. If you can run the book's projects you already have it; check with `java -version`.

1. Download `much-ado-practice-shop-1.0.1.zip` from the [practice shop release](https://github.com/ejvkamp/playwright-java-framework/releases/tag/practice-shop-v1.0.1),
   unzip it, and open a terminal in the unzipped folder (it holds `practice-shop.jar`, this README and the LICENSE).
   Already cloned this repository? `practice-shop.jar` is in this `much-ado-practice-shop` folder too: open a terminal there and go to step 2.
2. Start it:

   ```
   java -jar practice-shop.jar
   ```

3. Open <http://localhost:8085/>. Stop it with `Ctrl+C`.

That is all. There is nothing to install, no database and no Docker.

### A different port

```
java -jar practice-shop.jar 9090
java -jar practice-shop.jar --port 9090
```

On macOS/Linux you can also set `PORT=9090`; on Windows PowerShell use `$env:PORT = "9090"` before starting.
If the port is taken you will see `Port 8085 is already in use`, and the shop exits. Pick another port.

### Point your tests at it

The book's projects read the address from `src/test/resources/config/config.properties`:

```
baseUrl=http://localhost:8085/
```

If you used another port, change it there. Some of the book's test classes and TestNG suite files also contain the address
directly, so search the project for `8085` and replace it with your port everywhere it appears.

## What is in the shop

* **The store.** 75 products in 8 categories, search with autocomplete, a price slider, in-stock / category / manufacturer / rating filters
  with live counts, a cart, checkout, orders and read-only invoices. A phone-sized window gets a mobile layout.
* **Accounts.** Register a customer (there are no pre-made customers), sign in, view order history. Registration also works as a plain
  form POST to `index.php?route=account/register`, which is what the API chapter uses.
* **A little messy on purpose.** Real sites have accessibility problems, so this one has a small, fixed set (see below) for the book's axe scan to find.
  Everything else is clean, and the shop stays usable with a keyboard and a screen reader.
* **A REST API**, shaped like the public JSONPlaceholder service: `GET /users`, `/users/1`, `/posts`, `/posts?userId=3`,
  `POST /posts`, `PUT|PATCH|DELETE /posts/1`. Writes are echoed back but not stored.
  Writes need `Authorization: Bearer secret-token-12345` (or the token from `POST /auth/login` with
  `{"username":"testuser","password":"testpass"}`); without it you get `401`.

## Handy addresses

| Address | What it does |
|---|---|
| `/health` | Answers `ok` while the shop is up. |
| `/version` | The shop's release version. |
| `/reset` | Forgets every customer, cart and order. Run it for a clean slate between practice sessions. |

Everything lives in memory, so stopping and restarting the shop also gives you a clean slate.

## Options

Put these before `-jar`:

| Option | Effect |
|---|---|
| `-Dslider.delay=0` | The price slider is slowed on purpose (9 ms of work per key press), so long key-press loops take a while. `0` turns that off. |
| `-Dapi.auth=false` | Turns off the bearer-token requirement on the REST API, to practice without it. |
| `-Da11y=clean` | Switches off every planted accessibility problem, for a spotless shop. |
| `-Dquirks=off` | Switches off every planted quirk (see below), for a shop that behaves. The book's debugging walkthroughs will then not fail where the book says they do. |
| `-Dhydrate.delay=800` | How long (ms) the product page's quantity buttons stay dead after the page loads. |
| `-Dipv6=false` | The shop listens on both `127.0.0.1` and `::1` (both are this computer only), so `localhost` connects at once whichever your machine tries first. Use this to listen on `127.0.0.1` only, if `::1` misbehaves on your machine. |
| `-Dtrace=true` | Prints one line per request (time, method, address, how long the shop took), for chasing slow or missing requests. |

Example: `java -Dslider.delay=0 -jar practice-shop.jar`

## Planted accessibility problems

A page that passes every check teaches nothing, so the shop carries a few problems on purpose, where the book's scan looks. The scan
(`checkAccessibility` in the book) logs them and the test carries on. They are the same every time you start the shop.

| Where | axe rule | Impact | What is wrong |
|---|---|---|---|
| Home page | `aria-valid-attr` | critical | The "Start Shopping" button has a misspelled attribute, `aria-lable` |
| Home page, and every product list | `color-contrast` | serious | The prices on the product cards are a pale pink: 8 elements on the home page, up to 16 on a list |
| Home page | `heading-order` | moderate | The Featured product titles jump from a level-2 heading to level 4 |
| Home page | `region` | moderate | The yellow announcement bar sits outside every landmark |
| Checkout | `label` (critical), `label-title-only` (serious) | | The comment box's label is not connected to the box |

Every other page and state is clean. `-Da11y=clean` removes all of the above.

## Planted quirks

The book teaches debugging by letting your first attempts fail, the way they would on a real site. So the shop misbehaves on purpose in a few
places, always in the same way. None of it changes how the shop looks. `-Dquirks=off` removes all of it.

| Where | What you run into | Why |
|---|---|---|
| Search box | `getByPlaceholder("Search For Products")` matches 2 elements (strict mode violation) | There is a second search box for the phone layout |
| Filters on a search page | `getByLabel("In stock")` matches 2 elements | The filter panel exists twice (one copy is hidden) |
| Search results | Tick "In stock" and click the first result straight away: you land on a sold-out product and Add to Cart times out | A sold-out product leads the unfiltered list, and the list takes a moment to refresh |
| Empty cart | `getByText("Your shopping cart is empty!")` matches 2 elements | The header carries a cart preview for screen readers |
| Filters on a category page | `#mz-filter-panel-0-4` (In stock) times out | The panel is `-0-5` there; `[id^='mz-filter-panel-0-']` works on both |
| Product cards | `getByRole(LINK, "MacBook Pro")` matches 2 elements; the exact name matches 1 | The picture is a link too, and its name is the product name twice |
| Product page | Clicking "Increase quantity" straight after the page loads does nothing, with no error | The buttons start working about a second after the page loads; wait for the network to go idle. The markup also holds a hidden second pair of buttons |
| Cart | `getByRole(ROW)` filtered by a product name matches 2 rows | The header's cart preview lists the product as well; `last()` is the cart's own row |

## Things to know

* **Cloud browsers cannot see `localhost`.** A browser running in a cloud grid (such as the cloud chapter's) has no route to your computer.
  The cloud examples use a public practice site for that reason.
* **Page size and counts** are fixed: 16 products per page, 75 products in all. The book's assertions rely on them.
* The shop is tested with Chromium. Other browsers should work but are not part of the automated checks yet.
* The jar is built for Java 17 and has been tested on Java 25.
