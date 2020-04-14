describe('Student walkthrough', () => {
  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft')
    cy.contains('Logout').click()
  })

  it('list available tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.wait(100)
    cy.createTournament('Demo tournament 2', '20')
    cy.wait(100)
    cy.createTournament('Demo tournament 3', '10')
    cy.wait(100)
    cy.listAvailableTournaments()
    cy.wait(3000)
  })

  it('list enrolled tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.wait(100)
    cy.createTournament('Demo tournament 2', '20')
    cy.wait(100)
    cy.createTournament('Demo tournament 3', '10')
    cy.wait(100)
    cy.listEnrolledTournaments()
    cy.wait(3000)
  })

  it('list own tournaments', () => {
    cy.demoStudentLogin()
    cy.createTournament('Demo tournament 1', '30')
    cy.wait(100)
    cy.createTournament('Demo tournament 2', '20')
    cy.wait(100)
    cy.createTournament('Demo tournament 3', '10')
    cy.wait(100)
    cy.listOwnTournaments()
    cy.wait(3000)
  })

  it('list all tournaments', () => {
    cy.justDemoAdminLogin()
    cy.listAllTournaments()
    cy.wait(3000)
  })

});
