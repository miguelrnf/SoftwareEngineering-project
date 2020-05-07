describe('Student walkthrough', () => {
  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('list available tournaments', () => {
    cy.demoStudentLogin();
    cy.createTournament('Demo tournament 1', '30', true);
    cy.createTournament('Demo tournament 2', '20', true);
    cy.listAvailableTournaments();

    cy.assertAvailableEnrolled('Demo tournament 1');
    cy.assertAvailableEnrolled('Demo tournament 2');

    cy.deleteTournament('Demo tournament 1');
    cy.deleteTournament('Demo tournament 2');
  });

  it('list enrolled tournaments', () => {
    cy.demoStudentLogin();
    cy.createTournament('Demo tournament 1', '30', true);
    cy.createTournament('Demo tournament 2', '20', true);

    cy.listEnrolledTournaments();

    cy.listAvailableTournaments();
    cy.deleteTournament('Demo tournament 1');
    cy.deleteTournament('Demo tournament 2');
  });

  it('list own tournaments', () => {
    cy.demoStudentLogin();
    cy.createTournament('Demo tournament 1', '30', true);
    cy.createTournament('Demo tournament 2', '20', true);

    cy.listOwnTournaments();

    cy.assertOwn('Demo tournament 1');
    cy.assertOwn('Demo tournament 2');

    cy.deleteTournament('Demo tournament 1');
    cy.deleteTournament('Demo tournament 2');
  });

  it('list all tournaments', () => {
    cy.demoStudentLogin();
    cy.createTournament('Demo tournament 1', '30', true);
    cy.createTournament('Demo tournament 2', '20', true);

    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();

    cy.justDemoAdminLogin();
    cy.listAllTournaments();

    cy.assertAny('Demo tournament 1');
    cy.assertAny('Demo tournament 2');

    cy.deleteTournament('Demo tournament 1');
    cy.deleteTournament('Demo tournament 2');
  });
});
