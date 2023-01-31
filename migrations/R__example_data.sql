INSERT INTO transactions(id, owner, direction, amount, currency, booked_at, title) VALUES
('5b1bd521-c850-4406-ae1c-380bca99466a', 'user_A', 0, 1200, 'EUR', '2020-01-01T12:00', 'Transaction #1'),
('c8b8f89b-6e20-4948-a27f-b4f68fdaa348', 'user_A', 0, 100, 'EUR', '2020-01-01T13:00', 'Transaction #2')
ON CONFLICT DO NOTHING;
