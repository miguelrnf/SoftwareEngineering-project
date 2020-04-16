describe('Student walkthrough', () => {

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()
  })

  it('list available tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.createTournament('Demo tournament 2', '20')
    cy.listAvailableTournaments()

    cy.assertAvailableEnrolled('Demo tournament 1')
    cy.assertAvailableEnrolled('Demo tournament 2')

    cy.deleteTournament('Demo tournament 1')
    cy.deleteTournament('Demo tournament 2')
  })

  it('list enrolled tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.createTournament('Demo tournament 2', '20')

    cy.listEnrolledTournaments()
    cy.closeErrorMessage()

    cy.listAvailableTournaments()
    cy.deleteTournament('Demo tournament 1')
    cy.deleteTournament('Demo tournament 2')
  })

  it('list own tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.createTournament('Demo tournament 2', '20')

    cy.listOwnTournaments()

    cy.assertOwnAny('Demo tournament 1')
    cy.assertOwnAny('Demo tournament 2')

    cy.deleteTournament('Demo tournament 1')
    cy.deleteTournament('Demo tournament 2')
  })

  it('list all tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.createTournament('Demo tournament 2', '20')

    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()

    cy.justDemoAdminLogin()
    cy.listAllTournaments()

    cy.assertOwnAny('Demo tournament 1')
    cy.assertOwnAny('Demo tournament 2')

    cy.deleteTournament('Demo tournament 1')
    cy.deleteTournament('Demo tournament 2')
  })

});
