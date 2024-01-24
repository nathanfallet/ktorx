package me.nathanfallet.ktorx.controllers.auth

interface IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload> :
    IAuthController<LoginPayload, RegisterPayload>
