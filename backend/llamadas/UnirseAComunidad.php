<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['CodInvitacion'])){
                        $bbdd = new DBOperations();
			$comunidad = $bbdd->buscarCodigoInvitacion($_POST['CodInvitacion']);

                        if($comunidad  == false || $comunidad == 0){
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "Este código no pertenece a ninguna comunidad.";
                        }else{
                                if ($bbdd->anadirUsuarioAComunidad($_POST['IdUsuario'],$comunidad,0,'Usuario')){
                                        $respuesta['error'] = false;
                                        $respuesta['mensaje'] = "El usuario se ha añadido correctamente a la comunidad.";
                                }else{
                                        $respuesta['error'] = true;
                                        $respuesta['mensaje'] = "El usuario ya pertenece a la comunidad a la que desea entrar.";
                                }
	                }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
