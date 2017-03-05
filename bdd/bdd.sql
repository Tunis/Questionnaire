CREATE TABLE liaison
(
  idLiaison              INT(11) NOT NULL AUTO_INCREMENT,
  idquestionnaireLiaison INT(11) NOT NULL,
  idquestionLiaison      INT(11) NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (idLiaison, idquestionnaireLiaison, idquestionLiaison),
  CONSTRAINT idquestionnaireLiaison FOREIGN KEY (idquestionnaireLiaison) REFERENCES questionnaire (idQuestionnaire),
  CONSTRAINT idquestionLiaison FOREIGN KEY (idquestionLiaison) REFERENCES question (idQuestion)
);
CREATE INDEX idquestionLiaison_idx
  ON liaison (idquestionLiaison);
CREATE INDEX idquestionnaireLiaison_idx
  ON liaison (idquestionnaireLiaison);
CREATE TABLE question
(
  idQuestion   INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  textQuestion MEDIUMTEXT          NOT NULL
);
CREATE UNIQUE INDEX idQuestion_UNIQUE
  ON question (idQuestion);
CREATE TABLE questionnaire
(
  idQuestionnaire INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE TABLE reponse
(
  idReponse         INT(11) PRIMARY KEY    NOT NULL AUTO_INCREMENT,
  idquestionReponse INT(11)                NOT NULL,
  textReponse       MEDIUMTEXT             NOT NULL,
  verifReponse      TINYINT(4) DEFAULT '0' NOT NULL,
  CONSTRAINT idQuestion FOREIGN KEY (idquestionReponse) REFERENCES question (idQuestion)
    ON DELETE CASCADE
);
CREATE INDEX idQuestion_idx
  ON reponse (idquestionReponse);
CREATE TABLE score
(
  idScore              INT(11) PRIMARY KEY                NOT NULL AUTO_INCREMENT,
  iduserScore          INT(11)                            NOT NULL,
  timeScore            INT(11) DEFAULT '0'                NOT NULL,
  scoreScore           INT(11)                            NOT NULL,
  dateScore            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  idquestionnaireScore INT(11)                            NOT NULL,
  CONSTRAINT iduserScore FOREIGN KEY (iduserScore) REFERENCES users (idUser)
    ON DELETE CASCADE,
  CONSTRAINT idquestionnaireScore FOREIGN KEY (idquestionnaireScore) REFERENCES questionnaire (idQuestionnaire)
);
CREATE INDEX idquestionnaireScore_idx
  ON score (idquestionnaireScore);
CREATE INDEX iduserScore_idx
  ON score (iduserScore);
CREATE TABLE users
(
  idUser        INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  nameUser      VARCHAR(50)         NOT NULL,
  firstnameUser VARCHAR(50)         NOT NULL,
  pseudoUser    VARCHAR(50)         NOT NULL,
  passwordUser  VARCHAR(255)        NOT NULL
);
CREATE UNIQUE INDEX User_idUser_uindex
  ON users (idUser);
CREATE UNIQUE INDEX User_pseudoUser_uindex
  ON users (pseudoUser);