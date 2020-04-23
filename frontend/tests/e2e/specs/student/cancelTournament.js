describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()
  })

  it('login, creates and cancel a Tournament', () => {

    cy.createTournament('Demo tournament', '30')
    cy.cancel('Demo tournament')
    cy.deleteTournament('Demo tournament')
  })

});
