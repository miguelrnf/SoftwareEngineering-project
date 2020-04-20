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
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})


Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

//################################################# Suggestion feature

Cypress.Commands.add('demoStudentLoginSuggestion', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
    cy.get('[data-cy="quizzes"]').click()
    cy.contains('Suggestions').click()
})

Cypress.Commands.add('demoTeacherLoginSuggestion', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherButton"]').click()
    cy.get('[data-cy="management"]').click()
    cy.contains('Suggestions').click()
})

Cypress.Commands.add('createSuggestion', (content) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="content"]').type(content)
    cy.get('[data-cy="topics"]').type('a'.concat('{downarrow}{enter}'))
    cy.get('[data-cy="saveButton"]').click()
})



Cypress.Commands.add('createBlankSuggestion', (content) => {
    cy.visit('/')
    cy.get('[data-cy="Quizzes"]').click()
    cy.contains('Suggestions').click()
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('QuickApproveSuggestion', (content) => {
    cy.contains(content)
    cy.get('[data-cy="quickApproveButton"]').first().click({force: true})
})

Cypress.Commands.add('QuickRejectSuggestion', (content) => {
    cy.contains(content)
    cy.get('[data-cy="quickRejectButton"]').first().click({force: true})

})

Cypress.Commands.add('ShowSuggestion', (content) => {
    cy.contains(content)
    cy.get('[data-cy="showSuggestionButton"]').first().click({force: true})

})

Cypress.Commands.add('CloseSuggestion', (content) => {
    cy.get('[data-cy="closeSuggestionButton"]').click({force: true})

})

Cypress.Commands.add('ApproveSuggestion', (content) => {
    cy.get('[data-cy="approveSuggestionButton"]').click({force: true})

})

Cypress.Commands.add('RejectSuggestion', (content) => {
    cy.get('[data-cy="rejectSuggestionButton"]').click({force: true})

})



