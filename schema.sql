-- Schema for Rumour Tracking assignment
-- Tables mirror the CSV files in the project

CREATE TABLE Users (
    userId VARCHAR(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL -- 'GENERAL' or 'VERIFIER'
);

CREATE TABLE Rumours (
    rumourId CHAR(8) PRIMARY KEY,
    topic TEXT NOT NULL,
    source VARCHAR(100),
    createdDate DATE NOT NULL,
    credibilityScore INT NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'NORMAL' or 'PANIC'
    verifiedResult VARCHAR(20) NOT NULL -- 'UNVERIFIED','TRUE_INFO','FALSE_INFO'
);

CREATE TABLE Reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reporterUserId VARCHAR(16) NOT NULL,
    rumourId CHAR(8) NOT NULL,
    reportDate DATE NOT NULL,
    reportType VARCHAR(20) NOT NULL, -- 'DISTORTION','INCITEMENT','FALSE_INFO'
    FOREIGN KEY (reporterUserId) REFERENCES Users(userId),
    FOREIGN KEY (rumourId) REFERENCES Rumours(rumourId)
);

-- Example indexes
CREATE INDEX idx_reports_rumour ON Reports(rumourId);
CREATE INDEX idx_reports_user ON Reports(reporterUserId);
