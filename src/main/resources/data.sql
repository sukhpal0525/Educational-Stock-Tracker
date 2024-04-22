SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `User`;
TRUNCATE TABLE `Role`;
TRUNCATE TABLE `User_Roles`;
TRUNCATE TABLE `transactions`;
TRUNCATE TABLE `Portfolio`;
TRUNCATE TABLE `PortfolioItem`;
TRUNCATE TABLE `PortfolioStock`;
TRUNCATE TABLE `learning_content`;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `User` (`UserID`, `isAdmin`, `Username`, `Password`, `balance`) VALUES
(1, 1, 'admin', '$2a$10$AOYMzvkn8MISJsQR3Q0kXO0ZmFMJXQO6mw7IH/p2PfpJO.zCfrLlK', 10000),
-- Test User    (Password: test)
(2, 0, 'test', '$2a$12$yx2YbPbRSvI/1aCJXV7SeOe9y51tIqWtOEhJCm5kSLCDwOCZsSNKO', 10000);


--Insert stocks into the PortfolioStock table
INSERT INTO PortfolioStock (ticker, name, currentPrice, sector, fiftyTwoWeekHigh, fiftyTwoWeekLow, regularMarketChangePercent) VALUES
    ('AAPL', 'Apple Inc.', 145.00, 'Technology', 180.00, 130.00, 3.50),
    ('MSFT', 'Microsoft Corp.', 250.00, 'Technology', 320.00, 200.00, 4.00),
    ('AMZN', 'Amazon.com Inc.', 3100.00, 'Consumer Discretionary', 3550.00, 2800.00, 5.00),
    ('TSLA', 'Tesla Inc.', 700.00, 'Consumer Discretionary', 900.00, 600.00, 6.00),
    ('JNJ', 'Johnson & Johnson', 160.00, 'Healthcare', 180.00, 130.00, 2.00),
    ('PFE', 'Pfizer Inc.', 40.00, 'Healthcare', 60.00, 30.00, 1.50),
    ('XOM', 'Exxon Mobil Corp.', 60.00, 'Energy', 70.00, 40.00, 7.00),
    ('CVX', 'Chevron Corp.', 120.00, 'Energy', 130.00, 100.00, 3.00),
    ('JPM', 'JPMorgan Chase & Co.', 110.00, 'Financials', 160.00, 90.00, 4.50),
    ('GS', 'Goldman Sachs Group Inc.', 330.00, 'Financials', 390.00, 300.00, 2.50),
    ('PG', 'Procter & Gamble Co.', 130.00, 'Consumer Goods', 145.00, 120.00, 1.75),
    ('KO', 'Coca-Cola Co.', 50.00, 'Consumer Goods', 60.00, 45.00, 2.25);

-- Insert a portfolio for the user
INSERT INTO Portfolio (user_id, totalCost, totalValue, totalChangePercent)
VALUES (2, 0, 0, 0);

SET @portfolio_id = LAST_INSERT_ID();

-- Calculate the total cost of purchase
SET @total_cost = 2 * 173.31;

-- Update user balance for user_id = 2
-- Update `User` balance, subtracting the @total_cost from user's balance
UPDATE `User` SET balance = balance - 7950 WHERE UserID = 2;
-- UPDATE `User` SET balance = balance - @total_cost WHERE UserID = 2;

-- Insert a PortfolioItem that references the user's portfolio and the stock
INSERT INTO PortfolioItem (portfolio_id, stock_ticker, quantity, purchasePrice, isEditing) VALUES
    (@portfolio_id, 'AAPL', 10, 140, 0),
    (@portfolio_id, 'MSFT', 5, 240, 0),
    (@portfolio_id, 'AMZN', 1, 3000, 0),
    (@portfolio_id, 'TSLA', 3, 680, 0),
    (@portfolio_id, 'JNJ', 7, 155, 0),
    (@portfolio_id, 'PFE', 15, 38, 0),
    (@portfolio_id, 'XOM', 20, 58, 0),
    (@portfolio_id, 'CVX', 6, 115, 0),
    (@portfolio_id, 'JPM', 12, 105, 0),
    (@portfolio_id, 'GS', 4, 320, 0),
    (@portfolio_id, 'PG', 8, 125, 0),
    (@portfolio_id, 'KO', 25, 48, 0);

INSERT INTO `transactions` (`user_id`, `dateTime`, `stockTicker`, `quantity`, `purchasePrice`, `totalCost`, `transactionType`) VALUES
    (2, NOW(), 'MSFT', 5, 280.00, 1400.00, 'Buy'),
    (2, NOW(), 'GOOGL', 8, 120.00, 960.00, 'Buy'),
    (2, NOW(), 'PFE', 15, 45.00, 675.00, 'Buy'),
    (2, NOW(), 'JNJ', 12, 170.00, 2040.00, 'Buy'),
    (2, NOW(), 'JPM', 20, 130.00, 2600.00, 'Buy'),
    (2, NOW(), 'GS', 7, 320.00, 2240.00, 'Buy'),
    (2, NOW(), 'KO', 25, 60.00, 1500.00, 'Buy'),
    (2, NOW(), 'PG', 10, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'XOM', 18, 90.00, 1620.00, 'Buy'),
    (2, NOW(), 'CVX', 14, 115.00, 1610.00, 'Buy'),

    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'AAPL', 5, 150.00, 1500.00, 'Buy'),
    (2, NOW(), 'BA', 9, 200.00, 1800.00, 'Sell');

INSERT INTO learning_content (title, category, summary, detail) VALUES
    ('Basics of Investing', 'Investing-Basics', 'Start your investment journey by understanding the basics.', '<h5>Investment Types:</h5><ul><li><strong>Stocks:</strong> Investments that offer a share in the company’s profits.</li><li><strong>Bonds:</strong> Fixed investments that are considered less risky than stocks.</li><li><strong>Mutual Funds:</strong> Pooled money from many investors to invest in a diversified portfolio of stocks and bonds.</li><li><strong>ETFs:</strong> Exchange-traded funds that are similar to mutual funds but traded like individual stocks.</ul><p>Understanding your <strong>risk tolerance</strong> and setting <strong>clear financial goals</strong> are key to developing a diversified portfolio that aligns with your investment objectives.</p>'),
    ('The Importance of Sector Allocation', 'Investing-Basics', 'Understand why investing across various sectors is crucial.', '<p><strong>Sector Allocation</strong> is a strategy of diversifying your investment across different sectors to reduce risk and capitalise on growth. It’s essential because:</p><ul><li>Different sectors react differently to economic cycles.</li><li>Diversification can reduce the impact of poor performance in one sector.</li></ul><p>Examples of sectors include technology, healthcare, finance, and energy.</p>'),
    ('Why Use a Price Chart', 'Investing-Basics', 'Learn why price charts are a must when looking to buy stocks.', '<p>Price charts offer visual insights into a stock’s history. Key aspects include:</p><ul><li><strong>Vertical Axis:</strong> Price levels.</li><li><strong>Horizontal Axis:</strong> Time period.</li></ul><p>Look for <em>uptrends</em>, <em>downtrends</em>, and <em>sideways movements</em> to see how a stock is performing compared to yesterday, last week, last month, etc.</p>'),
    ('Creating an Investment Plan', 'Investing-Basics', 'Learn how to create a solid investment plan tailored to your goals.', '<h5>Start with SMART Goals:</h5><ul><li><strong>Specific:</strong> Clearly define what you want to achieve.</li><li><strong>Measurable:</strong> Ensure that you can track your progress.</li><li><strong>Achievable:</strong> Set goals that are realistic and attainable.</li><li><strong>Relevant:</strong> Your goals should align with your financial needs and aspirations.</li><li><strong>Time-bound:</strong> Set a timeframe for achieving your goals.</li></ul><p>Assess your risk tolerance and decide on an appropriate asset allocation. Regularly review and adjust your plan to ensure it remains aligned with your goals and current market conditions.</p>'),
    ('Understanding Your Portfolio Report', 'Investing-Basics', 'Grasp the basics of reading and understanding your portfolio report.', '<h5>Key Information:</h5><ul><li><strong>Name:</strong> Shows the name of each stock you own.</li><li><strong>Ticker:</strong> The stock ticker associated with all your owned stocks.</li><li><strong>Quantity:</strong> How many units of each stock you own.</li><li><strong>Purchased At:</strong> The exact price you purchased that stock at in dollars.</li><li><strong>Cost:</strong> How much money in dollars was spent on purchasing that stock.</li><li><strong>Value:</strong> The combined value of that stock factoring in its purchase price and quantity purchased.</li><li><strong>Change $:</strong> How much in dollars your investment compares to the current market price.</li><li><strong>Change %:</strong> How much your stock differs in value to the current market price expressed as a % value.</li><li><strong>Allocation:</strong> How much of that specific stock makes up your total portfolio holdings.</li></ul>'),


    ('Reading a Price Chart', 'Investing-Basics', 'Learn how to understand stock price charts.', '<p>Understanding price charts is important for any investor looking to invest in a stock. A price chart visualises the price movement of a stock over a specified period, and reading and understanding this data can give you insights into market trends and potential future movements.</p>
    <p>What should you first notice on a price chart? The <strong>vertical axis</strong>, which typically represents the price levels. This axis will show you the range of prices the stock has experienced. It’s important to look at the highest and lowest points to gauge the stock’s volatility: How much has it gone up or down in the given period?</p>
    <p>Then, observe the <strong>horizontal axis</strong>, where you’ll find the timeline. This could range from a single day to over a decade.
    This axis can vary from a day to up to 10 years on this website. This perspective allows you to consider the stock’s performance within different time frames. By looking at a longer timeline, you can see the overall trajectory of the stock''s value — has it generally gone up, down, or remained stable? Longer timelines can provide a broader view of the stock’s performance and can be essential for understanding long-term trends, so try and look out for sudden past price movements.</p>
    <p>Now, what are uptrends and downtrends? An <em>uptrend</em> is when the chart shows a consistent increase in stock price over time — a sign that the stock is doing well. Conversely, a <em>downtrend</em> is a consistent decline, which might indicate underlying issues or a bear market. <em>Sideways movements</em>, or a flat line, suggest stability but might also indicate a lack of momentum or market interest. This is where due-dilligence and researching the stock might help you out, try and look into the history of the company and look for the reason for stability or year-on-year decline.</p>
    <p>It’s also beneficial to look for patterns, such as <em>peaks and troughs</em> — consecutive high and low points can indicate cycles in the stock’s performance. Ask yourself: Does the stock bounce back from lows quickly, or does it struggle to regain value?</p>
    <p>Finally, remember that past performance isn’t always indicative of future results. Use the information from the price charts on this website as one of several tools in your investment decision-making process.</p>'),

    ('Understanding Market Risk', 'Risk', 'Learn what market risk is and how it affects your investments.', '<p>Market risk involves the potential for losses due to economic, political, or natural factors. Unlike specific risk, <strong>market risk</strong> affects all investments and requires strategies like <em>asset allocation</em> and <em>diversification</em> for mitigation.</p>'),
    ('Diversifying Across Sectors to Mitigate Risk', 'Risk', 'Understand how diversification across different sectors can mitigate investment risks.', '<p>Diversifying your investment across various sectors is crucial to mitigate risk. This strategy helps to spread out the potential for loss, as different sectors react differently to economic changes. For example, while the technology sector might be booming, the energy sector might be experiencing a downturn.</p>'),
    ('Assessing Risk with 52-Week Highs and Lows', 'Risk', 'Learn how the 52-week high and low values of a stock can indicate its volatility.', '<p>The 52-week high and low of a stock provide a snapshot of its volatility over the past year. A stock with a high difference between its 52-week high and low might indicate higher volatility. For example, if a stock’s 52-week high is $150 and its 52-week low is $50, the volatility can be significant.</p><p>To calculate a simple volatility measure, you might consider the percentage difference between the high and low. Using the above values: <code>((150 - 50) / 50) * 100 = 200%</code>. A higher percentage indicates higher volatility. YOu could use this information to assess risk and decide if a stock aligns with your risk tolerance.</p>'),
    ('Understanding Regular Market Percent Change', 'Risk', 'Explore how regular market percent change can inform your risk assessment of stocks.', '<p>The regular market percent change shows how much a stock’s price has moved compared to the previous trading day. This metric can give investors a sense of a stock''s short-term volatility. For instance, a stock with daily percent changes of -5%, +4%, -3%, and +5% might be more volatile than one with changes of -1%, +1%, -0.5%, and +0.75%. Consistent high volatility may indicate a higher risk.</p>'),
    ('Calculating Stock Volatility with High/Low Values and Market Percent', 'Risk', 'Learn to calculate a stock’s volatility using its high/low values and market percent changes.', '<p>Calculating a stock''s volatility can help understand its risk profile. One method combines the 52-week high/low range with regular market percent changes. For example, if a stock''s 52-week high is $120, its low is $80, and it regularly sees market percent changes of +/- 4%, its volatility can be significant.</p><p>Volatility calculation example: <code>((120 - 80) / 100) + 4% = 44%</code>. This calculation suggests that the stock is quite volatile, indicating a higher risk level. You might be looking for less risk may prefer stocks with lower volatility.</p>'),



    ('How to Use This Portfolio Tracker', 'Website-Help', 'Get to know how to make the most out of this portfolio tracker.', '<p>This portfolio tracker offers features to monitor and manage your investments. Features include:</p><ul><li>Adding and selling investments to and from your portfolio.</li><li>Tracking current performance for individual stocks.</li><li>Portfolio performance calculations to forecast future growth.</li><li>Utilising tools like the sector allocation chart for diversification insights.</li><li>Using the portfolio risk indicator tool to see how risky your portfolio is based on your investment decisions.</li><li>Generating a portfolio report as a PDF file to see a snapshot of your portfolio as a whole.</li></ul>'),
    ('Navigating the Website', 'Website-Help', 'Learn how to navigate through the website and access different pages.', '<p>This website is designed with easy navigation in mind, featuring links to key pages:</p><ul><li><strong>Home:</strong> The landing page with general information.</li><li><strong>Portfolio:</strong> View and manage your investment portfolio. <em>(Login required)</em></li><li><strong>My Orders:</strong> Review your buy/sell order history. <em>(Login required)</em></li><li><strong>Learn:</strong> Access educational content on investing.</li><li><strong>Login/Logout:</strong> Access your account or sign out.</li></ul><p>Note: You need to be logged in to see the Portfolio and Orders pages.</p>'),
    ('Signing Up and Logging In', 'Website-Help', 'Instructions for signing up and logging into the website.', '<p>To use this platform, you need to sign up first. Visit the <a href="/register">registration page</a> and enter a username, password, and confirm your password, then click the "Register" button. If you already have an account, click "Already have an account?" to navigate to the login page.</p>'),
    ('Using the Search Function', 'Website-Help', 'How to use the search bar for finding stocks.', '<p>Use the search bar at the top of the screen to enter a stock ticker or a valid stock name. Note:</p><ul><li>Invalid tickers or names will result in an error.</li><li>Valid queries will direct you to that stock’s page, showing historical data, performance, and buy/sell options.</li></ul>'),
    ('Understanding Your Balance', 'Website-Help', 'Learn about the virtual balance provided for trading.', '<p>All users start with a virtual balance of $10,000 to experiment with stock investments. This is <strong>not real money</strong> but is provided to help you learn and practice investing strategies with custom prices and quantities.</p>'),
    ('Reading the Portfolio Table', 'Website-Help', 'Understand the information presented in your portfolio table.', '<p>The portfolio table provides a detailed view of your investments, including:</p><ul><li>Asset name and quantity owned.</li><li>Purchase price versus current price.</li><li>Total gains or losses.</li></ul><p>This table is a tool for assessing performance and aligning investments with your strategy.</p>'),
    ('Understanding Your Transactions', 'Website-Help', 'Understanding Your Transactions',
     '<h5>Your Transaction History:</h5>
     <p>The <em>My Orders</em> page catalogs all your stock transactions, providing transparency and tracking of your investment actions over time.</p>
     <p><strong>Why is this important?</strong> Keeping a record of your transactions helps you to measure the performance and progression of your investments, aiding in strategic planning for future investment moves.</p>'),

    ('Using the Transactions Page', 'Website-Help', 'Navigating Your Transactions',
     '<h5>How to Navigate Your Transactions:</h5>
      <p>The transaction table shows each action you’ve taken with your investments, such as buying, selling, or editing stocks. It is ordered chronologically, with the latest transactions at the top and older transactions descending.</p>
      <p><strong>How to use:</strong> Use the search bar to quickly find transactions for a specific stock. The filter dropdown allows you to display only certain types of transactions, such as Buy, Sell or Edit. Rest assured, the system also tracks the type of edit that was performed, even if you added or removed more or less of that stock from your portfolio.</p>
      <p><strong>Pro Tip:</strong> If you want to analyse your investments or need to reconcile your portfolio, this page serves as a tool to reflect on your past decisions and inform future choices based on how well your previous decisions performed.</p>'),



    ('Portfolio Management Essentials', 'Portfolio-Management', 'Learn the fundamentals of managing your investment portfolio.', '<p>Effective portfolio management is crucial for achieving investment goals, involving:</p><ul><li>Regular strategy reviews.</li><li>Portfolio re-balancing.</li><li>Making decisions based on market conditions.</li></ul><p>Key factors include investment horizon, risk tolerance, and market trends.</p>'),
    ('Asset Allocation and Diversification', 'Portfolio-Management', 'Master the art of asset allocation and the principle of diversification.', '<h5>Asset Allocation:</h5><p>Asset allocation involves dividing an investment portfolio among different asset categories, such as stocks, bonds, and cash. The process depends largely on your investment goals and how much risk you are willing to take.</p><h5>Diversification:</h5><p>Diversification is a risk management technique that mixes a wide variety of investments within a portfolio. The rationale behind this technique is that a portfolio constructed of different kinds of assets will, on average, yield higher returns and pose a lower risk than any individual investment found within the portfolio.</p>'),
    ('Understanding and Managing Portfolio Risk', 'Portfolio-Management', 'Learn how to understand and manage the risks associated with your investment portfolio.', '<p>Understanding the types of risk your portfolio is exposed to can help you manage it effectively. Key risks include market risk, credit risk, and liquidity risk. Strategies for managing these risks include diversification, asset allocation, and maintaining a long-term perspective.</p>'),

    --finish this below
    ('Performance Measurement and Analysis', 'Portfolio-Management', 'Discover how to measure and analyse your portfolio’s performance.', '<p>Measuring and analysing your portfolio’s performance is crucial for understanding its efficacy. </p>'),
    ('Strategic Rebalancing of Your Portfolio', 'Portfolio-Management', 'Learn the importance of rebalancing and strategies for effectively rebalancing your portfolio.', '<p>Rebalancing is the process of realigning the weightings of a portfolio of assets. Rebalancing involves periodically buying or selling assets in a portfolio to maintain an original or desired level of asset allocation or risk. This can involve shifting funds from high-performing investments to those having underperformed to maintain a desired investment balance.</p>');
