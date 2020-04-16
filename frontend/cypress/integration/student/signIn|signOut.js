describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()
  })

  it('signIn in tournament', () => {
    cy.createTournament('Demo tournament 1', '30')
    cy.wait(100)
    cy.createTournament('Demo tournament 2', '20')
    cy.wait(100)
    cy.listAvailableTournaments()

    cy.signInSignOut('Demo tournament 1')

    cy.assertAvailableEnrolled('Demo tournament 1')

    cy.listAvailableTournaments()
  })

  it('signOut in tournament', () => {
    cy.listAvailableTournaments()
    cy.signInSignOut('Demo tournament 1')
    cy.closeErrorMessage()
    cy.listAvailableTournaments()
    cy.deleteTournament('Demo tournament 1')
    cy.deleteTournament('Demo tournament 2')
  })

});
