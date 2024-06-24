--company
create table if not exists companies(
    id bigserial PRIMARY KEY,
    symbol varchar(255),
    company_name varchar(255),
    is_enabled boolean,
    constraint unique_name unique (company_name)
);

--stock
create table if not exists stocks(
    id bigserial PRIMARY KEY,
    latestPrice real,
    change real ,
    changePercent real,
    company_name VARCHAR(255) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    unique (company_name)
);