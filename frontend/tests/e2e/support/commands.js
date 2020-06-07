// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('deleteTournament', title => {
  cy.contains(title)
    .get('[data-cy="id"]')
    .then($span => {
      const id = parseInt($span.text());
      cy.request('GET', 'http://localhost:8080/auth/demo/student').then(
        resp => {
          if (resp.status === 200)
            cy.request({
              method: 'DELETE',
              url: 'http://localhost:8080/tournaments/' + id + '/delete',
              auth: { bearer: resp.body.token }
            });
        }
      );
    });
});

//##############################TOURNAMENTS FEATURE BEGIN##########################################

Cypress.Commands.add('justDemoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoAdminLoginButton"]').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

Cypress.Commands.add('getTeacherTournament', () => {
  cy.get('[data-cy="management"]').click();
  cy.get('[data-cy="tournaments"]').click();
  cy.get('[data-cy="create"]').click();
});

Cypress.Commands.add('newQuestion', str => {
  cy.get('[data-cy="Student"]').click();
  cy.contains('Suggestions').click();
  cy.contains('').click();
});

Cypress.Commands.add(
  'createTournament',
  (title, numbQuestions, isStudentCreate, isEdit) => {
    let year =
      '#availableDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .datepicker-container-label > :nth-child(2) > .custom-button > .custom-button-content';
    let year2023 = '.flex-wrap > :nth-child(11)';
    let yearConc =
      '#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .datepicker-container-label > :nth-child(2) > .custom-button > .custom-button-content';

    if (isStudentCreate) {
      cy.contains('Tournament').click();
      cy.get('[data-cy="create"]').click();
    }

    if (isEdit) {
      cy.get('[data-cy="edit"]').click();
    }

    cy.get('[data-cy="title"]').type(title);
    cy.get('[data-cy="AssessmentTitle"]')
      .contains('Third mini-test')
      .click();
    cy.contains(numbQuestions).click({ force: true });
    cy.get('[data-cy=availableDate]').click();
    cy.get(year).click();
    cy.get(year2023).click();
    cy.get(
      '#availableDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(17)'
    ).click();
    cy.get(
      '#availableDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate'
    ).click();
    cy.get('[data-cy=conclusionDate]').click();
    cy.get(yearConc).click();
    cy.get(year2023).click();
    cy.get(
      '#conclusionDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(17)'
    ).click();
    cy.get(
      '#conclusionDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate'
    ).click();
    if (isEdit) {
      cy.get('[data-cy="editTournament"]').click();
    } else cy.get('[data-cy="createButton"]').click();
  }
);

Cypress.Commands.add('createInvalidTournament', (tile, numbQuestions) => {
  let year =
    '#availableDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .datepicker-container-label > :nth-child(2) > .custom-button > .custom-button-content';
  let year2023 = '.flex-wrap > :nth-child(11)';
  let year2019 = '.flex-wrap > :nth-child(7)';
  let yearConc =
    '#conclusionDateInput-picker-container-DatePicker > .calendar > .datepicker-controls > .datepicker-container-label > :nth-child(2) > .custom-button > .custom-button-content';

  cy.contains('Tournament').click();
  cy.contains('Create').click();
  cy.get('[data-cy="title"]').type(tile);
  cy.get('[data-cy="AssessmentTitle"]')
    .contains('Third mini-test')
    .click();
  cy.contains(numbQuestions).click();
  cy.get('[data-cy=availableDate]').click();
  cy.get(year).click();
  cy.get(year2019).click();
  cy.get(
    '#availableDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(17)'
  ).click();
  cy.get(
    '#availableDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate'
  ).click();
  cy.get('[data-cy=conclusionDate]').click();
  cy.get(yearConc).click();
  cy.get(year2023).click();
  cy.get(
    '#conclusionDateInput-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(17)'
  ).click();
  cy.get(
    '#conclusionDateInput-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .validate'
  ).click();
  cy.get('[data-cy="createButton"]').click();
});

Cypress.Commands.add('createBlankTournament', () => {
  cy.contains('Tournament').click();
  cy.contains('Create').click();
  cy.get('[data-cy="createButton"]').click();
});

Cypress.Commands.add('listAvailableTournaments', () => {
  cy.contains('Tournament').click();
  cy.contains('Created').click();
});

Cypress.Commands.add('listEnrolledTournaments', () => {
  cy.contains('Tournament').click();
  cy.contains('Enrolled').click();
});

Cypress.Commands.add('listOwnTournaments', () => {
  cy.contains('Tournament').click();
  cy.contains('Own').click();
});

Cypress.Commands.add('listAllTournaments', () => {
  cy.contains('Administration').click();
  cy.contains('Tournaments').click();
});

Cypress.Commands.add('signInSignOut', title => {
  cy.contains(title)
    .parent()
    .find('[data-cy="details"]')
    .click();
  cy.get('[data-cy="sign"]').click();
});

Cypress.Commands.add('cancel', title => {
  cy.contains(title)
    .parent()
    .get('[data-cy="cancel"]')
    .click();
});

Cypress.Commands.add('assertT', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 1);
});

//##############################TOURNAMENTS FEATURE END##########################################

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Post Cypress Commands
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

Cypress.Commands.add('demoStudentLoginPosts', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
  cy.get('[data-cy="Student"]').click();
});

Cypress.Commands.add('demoStudenDashboard', () => {
  cy.get('[data-cy="Dashboard"]').click();
});

Cypress.Commands.add('demoStudentLoginDashboard', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
  cy.get('[data-cy="Dashboard"]').click();
});

Cypress.Commands.add('demoTeacherLoginPosts', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
  cy.contains('Management').click();
});

Cypress.Commands.add('closeDialog', () => {
  cy.get('[data-cy="closeButton"]').click();
});

Cypress.Commands.add('gotoPosts', () => {
  cy.contains('Posts').click();
});

Cypress.Commands.add('gotoSubmitPost', () => {
  cy.contains('Submit Post').click();
});

Cypress.Commands.add('gotoPosts', () => {
  cy.contains('Posts').click();
});

Cypress.Commands.add('submitPost', (question, studentQuestion) => {
  cy.get('[data-cy="pickQ"]').type(question.concat('{downarrow}{enter}'));
  cy.get('[data-cy="typeQ"]').type(studentQuestion);
  cy.get('[data-cy="submitButton"]').click();
});

Cypress.Commands.add('deletePost', studentQuestion => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .parent()
    .find('[data-cy="deleteButton"]')
    .click({ force: true });
});

Cypress.Commands.add('answerPost', (studentQuestion, teacherAnswer) => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .parent()
    .find('[data-cy="showButton"]')
    .click({ force: true })
    .get('[data-cy="answerPostButton"]')
    .click({ force: true })
    .get('[data-cy="typeAnswer"]')
    .type(teacherAnswer.concat('{enter}{esc}'));
});

Cypress.Commands.add('viewPost', studentQuestion => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .parent()
    .find('[data-cy="showButton"]')
    .click({ force: true });
});

Cypress.Commands.add('comment', (type, comment) => {
  cy.get('[data-cy="'.concat(type).concat('Button"]'))
    .click({ force: true })
    .get('[data-cy="'.concat(type).concat('Box"]'))
    .click({ force: true })
    .type(comment.concat('{enter}{esc}'));
});

Cypress.Commands.add('editPost', (studentQuestion, newQuestion) => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .parent()
    .find('[data-cy="editButton"]')
    .click({ force: true })
    .get('[data-cy="dialogEditPost"]')
    .type('{selectall}{backspace}'.concat(newQuestion))
    .get('[data-cy="saveEditButton"]')
    .click({ force: true });
});

Cypress.Commands.add('editAnswer', (studentQuestion, newAnswer) => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .find('[data-cy="editAnswerButton"]')
    .click({ force: true })
    .get('[data-cy="editAnswerBox"]')
    .type('{selectall}{backspace}'.concat(newAnswer).concat('{enter}'));
});

Cypress.Commands.add('pressStatusButton', (studentQuestion, button) => {
  cy.contains(studentQuestion)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .parent()
    .find('[data-cy=StatusButtons]')
    .find('[data-cy="'.concat(button).concat('"]'))
    .click({ force: true });
});

Cypress.Commands.add('createQuiz', () => {
  cy.contains('Create')
    .click()
    .get('[data-cy="createQuizButton"]')
    .click()
    .get('[data-cy="endQuizButton"]')
    .click()
    .get('[data-cy="imSureButton"]')
    .click();
});

Cypress.Commands.add('checkPostsByQuiz', () => {
  cy.get('[data-cy="checkPostsByQuizButton"]').click();
});

//################################################# Suggestion feature

Cypress.Commands.add('demoStudentLoginSuggestion', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
  cy.get('[data-cy="Student"]').click();
  cy.contains('Suggestions').click();
});

Cypress.Commands.add('demoTeacherLoginSuggestion', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
  cy.get('[data-cy="management"]').click();
  cy.contains('Suggestions').click();
});

Cypress.Commands.add('createSuggestion', (title, content, option) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="titleTextArea"]').type(title, { force: true });
  cy.get('[data-cy="content"]').type(content, { force: true });
  cy.get('[data-cy="correctToggleButton"]')
    .first()
    .click({ force: true });
  cy.get('[data-cy="optionTextArea"]')
    .eq(0)
    .type(option, { force: true });
  cy.get('[data-cy="optionTextArea"]')
    .eq(1)
    .type(option, { force: true });
  cy.get('[data-cy="optionTextArea"]')
    .eq(2)
    .type(option, { force: true });
  cy.get('[data-cy="optionTextArea"]')
    .eq(3)
    .type(option, { force: true });
  cy.get('[data-cy="topics"]').type('a'.concat('{downarrow}{enter}'), {
    force: true
  });
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('listSuggestion', contentPart => {
  cy.get('[data-cy="search"]').type(contentPart.concat('{downarrow}{enter}'));
  cy.contains(contentPart)
    .get('[data-cy=showSuggButton]')
    .first()
    .click();
  cy.get('[data-cy="closeButton"]').click();
});

Cypress.Commands.add('notfoundSuggestion', contentPart => {
  cy.get('[data-cy="search"]').type(contentPart.concat('{downarrow}{enter}'));
});

Cypress.Commands.add('editSuggestion', (contentPart, newContent) => {
  cy.get('[data-cy="search"]').type(contentPart.concat('{downarrow}{enter}'));
  cy.contains(contentPart)
    .get('[data-cy=editSuggButton]')
    .first()
    .click();
  cy.get('[data-cy="content"]').type(newContent, { force: true });
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('editSuggestionPrivacy', contentPart => {
  cy.get('[data-cy="search"]').type(contentPart.concat('{downarrow}{enter}'));
  cy.contains(contentPart)
    .get('[data-cy=editSuggButton]')
    .first()
    .click();
  cy.get('[data-cy="togglePrivacyButton"]').click({ force: true });
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('changeToQuestion', contentPart => {
  cy.contains(contentPart)
    .parent()
    .parent()
    .parent()
    .find('[data-cy=addQuestionButton]')
    .click();
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createBlankSuggestion', content => {
  cy.get('[data-cy="Student"]').click();
  cy.contains('Suggestions').click();
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('QuickApproveSuggestion', content => {
  cy.contains(content);
  cy.get('[data-cy="quickApproveButton"]')
    .first()
    .click({ force: true });
});

Cypress.Commands.add('QuickRejectSuggestion', content => {
  cy.contains(content);
  cy.get('[data-cy="quickRejectButton"]')
    .first()
    .click({ force: true });
});

Cypress.Commands.add('ShowSuggestion', content => {
  cy.contains(content);
  cy.get('[data-cy="showSuggestionButton"]')
    .first()
    .click({ force: true });
});

Cypress.Commands.add('CloseSuggestion', content => {
  cy.get('[data-cy="closeSuggestionButton"]').click({ force: true });
});

Cypress.Commands.add('ApproveSuggestion', content => {
  cy.get('[data-cy="approveSuggestionButton"]').click({ force: true });
});

Cypress.Commands.add('RejectSuggestion', content => {
  cy.get('[data-cy="rejectSuggestionButton"]').click({ force: true });
});
