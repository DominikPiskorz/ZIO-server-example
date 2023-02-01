INSERT INTO transactions(id, owner, direction, amount, currency, booked_at, title) VALUES
('5b1bd521-c850-4406-ae1c-380bca99466a', 'user_A', 0, 1200, 'EUR', '2020-01-01T12:00', 'Transaction #1'),
('c8b8f89b-6e20-4948-a27f-b4f68fdaa348', 'user_A', 0, 100, 'EUR', '2020-01-02T13:00', 'Transaction #2'),
('8dd5552a-64d0-4688-be76-1118c92c88bc', 'user_A', 1, 110, 'PLN', '2020-01-01T10:00', 'aaaa'),
('b9419985-9dd0-4d0f-b8f1-076c690366c7', 'user_A', 0, 200, 'GBP', '2020-01-01T23:00', 'Transaction 999'),
('98a08cf6-8de0-44ba-8a21-48b10fb05487', 'user_A', 1, 3000, 'GBP', '2020-02-01T13:00', null),
('160291c3-a405-4442-8d31-4a4ddfe6bd67', 'user_A', 1, 4000000, 'USD', '2020-03-03T13:00', 'book'),
('06e57b1c-0790-4c2e-b660-51e93623999e', 'user_A', 0, 5, 'AUD', '2021-01-01T13:00', 'mouse')
ON CONFLICT DO NOTHING;
