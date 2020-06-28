describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and edit a Tournament', () => {
    cy.createTournament('Demo tournament', '30', true, false);
    cy.wait(100);
    cy.createTournament('Demo tournament', '10', false, true);
    cy.wait(100);
    cy.deleteTournament('Demo tournament');
  });
});
