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

Cypress.Commands.add('justDemoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
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

Cypress.Commands.add('deleteTournament', (title) => {
    cy.contains(title).find('#num').then(($span) => {
        const id = parseInt($span.text())
        cy.request('GET', 'http://localhost:8080/auth/demo/student').then((resp)=>{
            if(resp.status===200)
                cy.request({method:'DELETE', url: 'http://localhost:8080/tournaments/' + id + '/delete', auth:{ bearer: resp.body.token}})
        })
    })
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

    Cypress.Commands.add('demoStudentLogin', () => {
        cy.visit('/')
        cy.get('[data-cy="studentButton"]').click()
    })

    Cypress.Commands.add('createTournament', (tile, numbQuestions) => {
        let forwoardButtonAval = ':nth-child(3) > .v-btn__content > .v-icon'
        let forwoardButtonConc = '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon';

        cy.contains('Tournament').click()
        cy.contains('Create').click()
        cy.get('[data-cy="title"]').type(tile)
        cy.get('[data-cy="AssessmentTitle"]').contains('Third mini-test').click()
        cy.contains(numbQuestions).click()
        cy.get('.row > :nth-child(1) > .v-input').click()
        cy.get(forwoardButtonAval).click()
        cy.get(forwoardButtonAval).click()
        cy.get(forwoardButtonAval).click()
        cy.get(forwoardButtonAval).click()
        cy.get('.tab-transition-enter-active > tbody > :nth-child(2) > :nth-child(1) > .v-btn').click()
        cy.get('.green--text').click()
        cy.get(':nth-child(3) > .v-input').click()
        cy.get(forwoardButtonConc).click()
        cy.get(forwoardButtonConc).click()
        cy.get(forwoardButtonConc).click()
        cy.get(forwoardButtonConc).click()
        cy.get(forwoardButtonConc).click()
        cy.get('.tab-transition-enter-active > tbody > :nth-child(2) > :nth-child(1) > .v-btn').click()
        cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text').click()
        cy.get('[data-cy="createButton"]').click()
    })

    Cypress.Commands.add('createInvalidTournament', (tile, numbQuestions) => {
        let backbutton = '.v-date-picker-header > :nth-child(1) > .v-btn__content > .v-icon'
        let forwoardButton = '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon';

        cy.contains('Tournament').click()
        cy.contains('Create').click()
        cy.get('[data-cy="title"]').type(tile)
        cy.get('[data-cy="AssessmentTitle"]').contains('Third mini-test').click()
        cy.contains(numbQuestions).click()
        cy.get('.row > :nth-child(1) > .v-input').click()
        cy.get(backbutton).click()
        cy.get('.tab-reverse-transition-enter-active > tbody > :nth-child(1) > :nth-child(4) > .v-btn').click()
        cy.get('.green--text').click()
        cy.get(':nth-child(3) > .v-input').click()
        cy.get(forwoardButton).click()
        cy.get(forwoardButton).click()
        cy.get(forwoardButton).click()
        cy.get(forwoardButton).click()
        cy.get(forwoardButton).click()
        cy.get('.tab-transition-enter-active > tbody > :nth-child(2) > :nth-child(1) > .v-btn').click()
        cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text').click()
        cy.get('[data-cy="createButton"]').click()
    })

    Cypress.Commands.add('createBlankTournament', () => {
        cy.contains('Tournament').click()
        cy.contains('Create').click()
        cy.get('[data-cy="createButton"]').click()
    })

    Cypress.Commands.add('listAvailableTournaments', () => {
        cy.contains('Tournament').click()
        cy.contains('Available').click()
    })

    Cypress.Commands.add('listEnrolledTournaments', () => {
        cy.contains('Tournament').click()
        cy.contains('Enrolled').click()
    })

    Cypress.Commands.add('listOwnTournaments', () => {
        cy.contains('Tournament').click()
        cy.contains('Own').click()
    })

    Cypress.Commands.add('listAllTournaments', () => {
        cy.contains('Administration').click()
        cy.contains('All Tournaments').click()
    })

    Cypress.Commands.add('signInSignOut', (title) => {
        cy.contains(title).parent().find('[data-cy="details"]').click()
        cy.get('[data-cy="sign"]').click()
    })

    Cypress.Commands.add('assertOwnAny', (title) => {
        cy.contains(title).parent()
          .should('have.length', 1)
          .children()
          .should('have.length', 6)
    })

    Cypress.Commands.add('assertAvailableEnrolled', (title) => {
        cy.contains(title).parent()
          .should('have.length', 1)
          .children()
          .should('have.length', 5)
    })




