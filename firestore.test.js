const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read customers", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("customers").get());
});

test("Authenticated user: can create their customer with valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("customers").doc("cust_1").set({
      userId: ALICE_UID,
      name: "Ramesh Sharma",
      phone: "+919876543210",
      siteAddress: "Plot 42, Sukher, Udaipur",
      city: "Udaipur",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot read another user's customer", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("customers").doc("bob_cust").set({
      userId: BOB_UID,
      name: "Suresh Verma",
      phone: "+919811122233",
      siteAddress: "Sector 14, Hiran Magri",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("customers").doc("bob_cust").get());
});

test("Authenticated user: can query their own customers with matching filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("customers").doc("alice_cust").set({
      userId: ALICE_UID,
      name: "Dinesh Patel",
      phone: "+919922334455",
      siteAddress: "Bedla Road, Udaipur",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("customers").where("userId", "==", ALICE_UID).get()
  );
});

test("Any authenticated user can browse catalogue items", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("catalogue_items").doc("cat_1").set({
      userId: ALICE_UID,
      title: "Stainless Steel Glass Balcony Railing SS 304",
      category: "Stainless Steel Railing",
      baseRate: 750,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("catalogue_items").doc("cat_1").get());
});
