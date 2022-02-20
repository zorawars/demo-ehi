DROP TABLE patients IF EXISTS;

CREATE TABLE patients (
    id INTEGER IDENTITY PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL
);
CREATE INDEX patients_first_name ON patients(first_name);
