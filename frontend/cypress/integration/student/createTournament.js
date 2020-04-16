describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()
  })

  it('login and creates a Tournament', () => {

    cy.createTournament('Demo tournament', '30')
    cy.assertOwnAny('Demo tournament 1')
    cy.deleteTournament('Demo tournament')
  })

  it('try to create a tournament with an invalid date', () => {
    cy.log('try to create a tournament with an invalid date')

    cy.createInvalidTournament('Demo tournament incorrect', '30')
    cy.closeErrorMessage()

  })

  it('try to create a blank tournament', () => {
    cy.log('try to create a tournament with all fields blank')

    cy.createBlankTournament()
    cy.closeErrorMessage()

  })

});
