--companies
create table if not exists companies
(
    id           bigserial PRIMARY KEY,
    symbol       varchar(255),
    company_name varchar(255),
    is_enabled   boolean,
    constraint unique_name unique (company_name)
);

--stocks
create table if not exists stocks
(
    id             bigserial PRIMARY KEY,
    latest_price   real,
    change         real,
    change_percent real,
    company_name   VARCHAR(255) NOT NULL,
    symbol         VARCHAR(255) NOT NULL unique
);