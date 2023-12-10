print('START')

db = db.getSiblingDB('product-service');
db.createUser(
    {
        user: 'mongoadmin',
        password: 'password',
        roles: [{role: 'readWrite', db: 'product-service'}]
    }
)

db.createCollection('user');

print('END')